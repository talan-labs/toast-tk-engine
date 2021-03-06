package io.toast.tk.core.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestUtils {

	private static final Logger LOG = LogManager.getLogger(RestUtils.class);
	
	private static final int HTTP_CLIENT_MAX_POOL_SIZE = 25;
	public static final String WEBAPP_ADDR = "toast.webapp.addr";

	public static final String WEBAPP_PORT = "toast.webapp.port";


	public static void get(final String url) {
		final Client httpClient = Client.create();
		final WebResource webResource = httpClient.resource(StringEscapeUtils.escapeHtml3(url));
		final ClientResponse response = webResource.get(ClientResponse.class);
		final int statusCode = response.getStatus();
		LOG.debug("Client response code: {}", statusCode);
	}

	public static void postPage(final String webAppAddr, final String webAppPort, final String value,
			final Object[] selectedValues) {
		final Client httpClient = Client.create();
		final String webappURL = getWebAppURI(webAppAddr, webAppPort);
		final WebResource webResource = httpClient.resource(webappURL + "/api/saveNewInspectedPage");
		final InspectPage requestEntity = new InspectPage(value, Arrays.asList(selectedValues));
		final Gson gson = new Gson();
		final ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, gson.toJson(requestEntity));
		final int statusCode = response.getStatus();
		LOG.info("Client response code: {}", statusCode);
	}


	public static String downloadRepository(final String uri) {
		final Client httpClient = Client.create();
		final String jsonResponse = getJsonResponseAsString(uri, httpClient);
		JSONArray jsonResult;
		try {
			jsonResult = new JSONArray(jsonResponse);
			final StringBuilder builder = new StringBuilder(4096);
			for (int i = 0; i < jsonResult.length(); i++) {
				final String page = jsonResult.getString(i);
				builder.append(page);
			}
			return builder.toString();
		} catch (final JSONException e) {
			LOG.error(jsonResponse + "\n Exception catched: " + e.getMessage(), e);
		}
		return null;
	}

	public static String getJsonResponseAsString(final String uri, final Client httpClient) {
		final WebResource webResource = httpClient.resource(uri);
		final ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		final int statusCode = response.getStatus();
		if (statusCode == 401) {
			try {
				throw new AuthenticationException("Invalid Api Key!");
			} catch (final AuthenticationException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return response.getEntity(String.class);
	}

	public static boolean postScenario(final String scenarioName, final String webAppHost, final String webAppPort,
			final String scenarioSteps) {
		try {
			final Client httpClient = Client.create();
			final String webappURL = getWebAppURI(webAppHost, webAppPort);
			final WebResource webResource = httpClient.resource(webappURL + "/api/saveNewInspectedScenario");
			final Gson gson = new Gson();
			final InspectScenario scenario = new InspectScenario(scenarioName, scenarioSteps);
			final String json = gson.toJson(scenario);
			LOG.debug(json);
			final ClientResponse response = webResource.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
			final int statusCode = response.getStatus();
			LOG.info("Client response code: {}", statusCode);
			return statusCode >= 200 && statusCode < 400;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	public static String getWebAppURI(final String host, final String port) {
		return "http://" + host + ":" + port;
	}

	public static String getWebAppURI() {
		final String webAppAddr = System.getProperty(WEBAPP_ADDR);
		if (webAppAddr == null || webAppAddr.isEmpty()) {
			throw new RuntimeException(WEBAPP_ADDR + " system property isn't defined !");
		}
		final String webAppPort = System.getProperty(WEBAPP_PORT);
		if (webAppPort == null || webAppPort.isEmpty()) {
			throw new RuntimeException(WEBAPP_PORT + " system property isn't defined !");
		}
		return getWebAppURI(webAppAddr, webAppPort);
	}

	public static Collection<ImportedScenario> getListOfScenario() {
		try {
			final Client httpClient = Client.create();
			final String webappURL = getWebAppURI();
			final String response = getJsonResponseAsString(webappURL + "/api/loadScenariiList", httpClient);
			final Gson g = new Gson();
			final Type typeOfT = new TypeToken<Collection<ImportedScenario>>() {
				/* NOOP */}.getType();
			final Collection<ImportedScenario> scenarioList = g.fromJson(response, typeOfT);
			return scenarioList;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public static ImportedScenarioDescriptor getScenario(final ImportedScenario scenarioRef) {
		try {
			final Client httpClient = Client.create();
			final String webappURL = getWebAppURI();
			final String response = getJsonResponseAsString(webappURL + "/api/loadScenarioSteps/" + scenarioRef.getId(),
					httpClient);
			final Gson g = new Gson();
			final ImportedScenarioDescriptor scenarioDescriptor = g.fromJson(response,
					ImportedScenarioDescriptor.class);
			return scenarioDescriptor;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public static boolean post(final HttpRequest requestInfo) {
		CloseableHttpResponse response = null;
		try (final CloseableHttpClient httpClient = buildClient(requestInfo)) {
			HttpPost httppost = buildJsonHttpPost(requestInfo);
			response = httpClient.execute(httppost);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IllegalStateException(
						"Failed to post connectors - HTTP reply code : " 
				+ response.getStatusLine().getStatusCode());
			}
			return response.getStatusLine().getStatusCode() == 200;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	public static boolean postWebEventRecord(final HttpRequest requestInfo) {
		CloseableHttpResponse response = null;
		try (final CloseableHttpClient httpClient = buildClient(requestInfo)) {
			HttpPost httppost = buildJsonHttpPost(requestInfo);
			response = httpClient.execute(httppost);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IllegalStateException(
						"Failed to publish web record - HTTP reply code : " + response.getStatusLine().getStatusCode());
			}
			return response.getStatusLine().getStatusCode() == 200;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	public static boolean registerAgent(final HttpRequest requestInfo) {
		CloseableHttpResponse response = null;
		try (final CloseableHttpClient httpClient = buildClient(requestInfo)) {
			HttpPost httppost = buildJsonHttpPost(requestInfo);
			response = httpClient.execute(httppost);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IllegalStateException(
						"Agent registration error - HTTP reply code : " + response.getStatusLine().getStatusCode());
			}
			return response.getStatusLine().getStatusCode() == 200;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if(response != null){
					response.close();
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return false;
	}

	private static HttpPost buildJsonHttpPost(HttpRequest requestInfo) throws UnsupportedEncodingException, IllegalAccessException {
		if (Strings.isNullOrEmpty(requestInfo.getApiKey())) {
			throw new IllegalAccessException("No Api Key provided !");
		}
		HttpPost httppost = new HttpPost(requestInfo.getUri());
		httppost.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		httppost.setHeader("Token", requestInfo.getApiKey());

		StringEntity input = new StringEntity(requestInfo.getJson());
		input.setContentType("application/json");
		httppost.setEntity(input);
		return httppost;
	}

	private static CloseableHttpClient buildClient(final HttpRequest requestInfo) {
		HttpClientBuilder httpBuilder = HttpClients.custom();
        try {
			PoolingHttpClientConnectionManager cm = setHttpConnectionManager();
			httpBuilder.setConnectionManager(cm);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| KeyStoreException e) {
			LOG.error(e.getMessage());
		}

		if (isProxyDefined(requestInfo)) {
			HttpHost proxy = new HttpHost(requestInfo.getProxyAddress(), requestInfo.getProxyPort());
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			httpBuilder.setDefaultRequestConfig(config);
		}
		if (isCredentialDefined(requestInfo)) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			String user = requestInfo.getProxyUser();
			String passwd = requestInfo.getProxyPassword();
			credsProvider.setCredentials(new AuthScope(requestInfo.getProxyAddress(), requestInfo.getProxyPort()),
					new UsernamePasswordCredentials(user, passwd));
			httpBuilder.setDefaultCredentialsProvider(credsProvider);
		}
		return httpBuilder.build();
	}

	private static boolean isCredentialDefined(final HttpRequest requestInfo) {
		return StringUtils.isNotEmpty(requestInfo.getProxyPassword()) && StringUtils.isNotEmpty(requestInfo.getProxyUser());
	}

	private static boolean isProxyDefined(final HttpRequest requestInfo) {
		return StringUtils.isNotEmpty(requestInfo.getProxyAddress());
	}
	
	public static String downloadRepositoryAsWiki(String apiKey) {
		final String webappURL = getWebAppURI();
		return downloadRepository(webappURL + "/api/repository/all/" + apiKey);
	}
	
	public static void unRegisterAgent(String hostName) {
		// NO-OP
	}

	private static PoolingHttpClientConnectionManager setHttpConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		// This connection manager must be used if more than one thread will
	       // be using the HttpClient.
			SSLContextBuilder builder = SSLContexts.custom();
			builder.loadTrustMaterial(null, new TrustStrategy() {
			    @Override
			    public boolean isTrusted(X509Certificate[] chain, String authType)
			            throws CertificateException {
			        return true;
			    }
			});
			
			@SuppressWarnings("deprecation")
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	                builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			new PlainConnectionSocketFactory();
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
			        .<ConnectionSocketFactory> create().register("https", sslsf)
			        .register("http", PlainConnectionSocketFactory.getSocketFactory())
			        .build();

	        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	        cm.setMaxTotal(HTTP_CLIENT_MAX_POOL_SIZE);
	        //cm.setDefaultSocketConfig( SocketConfig.custom().setSoKeepAlive( true ).setSoReuseAddress( true ).setSoTimeout( 3000 ).build() 
	        //cm.setValidateAfterInactivity(1); // essai pour resoudre java.net.SocketException: Software caused connection abort: recv failed

	        new ConnectionKeepAliveStrategy() {
	        	public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
	                // Honor 'keep-alive' header
	                HeaderElementIterator it = new BasicHeaderElementIterator(
	                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
	                while (it.hasNext()) {
	                    HeaderElement he = it.nextElement();
	                    String param = he.getName();
	                    String value = he.getValue();
	                    if (value != null && param.equalsIgnoreCase("timeout")) {
	                        try {
	                            return Long.parseLong(value) * 1000;
	                        } catch(NumberFormatException ignore) {
	                        	LOG.warn(ignore.getLocalizedMessage());
	                        }
	                    }
	                }
	                return (long) 30 * 1000;
	            }
	        };
	        return cm;
	}
}