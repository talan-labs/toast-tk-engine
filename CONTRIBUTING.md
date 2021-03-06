# How to contribute

If you like ![Open Source Love](https://badges.frapsoft.com/os/v3/open-source.svg?v=103) projects and would like to give back some help, we'd like to see your contributions!

It doesn't matter how familiar you are with test automation applications, or whether you know how to write programs for Java. There are plenty of ways to be helpful! One of the first things you should do is actually use Toast TK, and get to know it - read about it, evangelise it, and engage with the wider community.

If you'd like to help, get in touch and let us know how you'd like to help. We love contributors!!

Here are the guidelines we would like you to follow:
 
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Contribution Guidelines](#contribute)
 - [Commit Message Guidelines](#commit)
 
## <a name="issue"></a> Reporting bugs

### Before submitting a bug report

- Search [open issues](https://github.com/toast-tk/toast-tk-engine/issues) to see if the problem has already been reported. If it has, add a comment to the existing issue instead of opening a new one.
- You can also tell us about your problem on [Gitter](https://gitter.im/toast-tk/toast-tk-engine).

### How to submit a bug report?

Bugs are tracked as GitHub issues. [Create an issue](https://github.com/toast-tk/toast-tk-engine/issues/new) on toast-tk-engine project and provide the following information.

Explain the problem and include additional details to help maintainers reproduce the problem:

Use a clear and descriptive title for the issue to identify the problem.
Describe the exact steps which reproduce the problem in as many details as possible.
Provide specific examples to demonstrate the steps.

## <a name="feature"></a> Suggesting enhancements

You can request a new feature by submitting an issue to our GitHub Repository.

If you would like to implement a new feature then consider what kind of change it is:
- Major Changes that you wish to contribute to the project should be discussed first on Gitter https://gitter.im/toast-tk/toast-tk-engine so that we can better coordinate our efforts, prevent duplication of work, and help you to craft the change so that it is successfully accepted into the project.
- Small Changes can be crafted and submitted to the GitHub Repository as a Pull Request.

### Submit an issue

Before you submit your issue search the archive, maybe your question was already answered.

## <a name="contribute"></a> Contribute to code

### Getting Started

If you want to contribute but do not know where to start, filter issues with the label `up-for-grabs`. These issues are easy enough to do for someone discovering the project.

### Working on an issue

- Assign yourself to the issue.
- Create a branch from the branch `snapshot` starting by the number of the issue, followed by a few words that describe the issue. For instance `77-scenario-replay`.
- When the development is finished, create a merge request from your branch to the branch `snapshots`


## <a name="commit"></a> Commit Message Guidelines

We have very precise rules over how our git commit messages can be formatted.  This leads to **more
readable messages** that are easy to follow when looking through the **project history**.  But also,
we use the git commit messages to **generate the application change log**.

### Commit Message Format
Each commit message consists of a **header**, a **body** and a **footer**.  The header has a special
format that includes a **type**, a **scope** and a **subject**:

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

The **header** is mandatory and the **scope** of the header is optional.

Any line of the commit message cannot be longer 100 characters! This allows the message to be easier
to read on GitHub as well as in various git tools.

Footer should contain a [closing reference to an issue](https://help.github.com/articles/closing-issues-via-commit-messages/) if any.

Samples: (even more [samples](https://github.com/angular/angular/commits/master))

```
docs(changelog): update change log to beta.5
```
```
fix(release): need to depend on latest rxjs and zone.js

The version in our package.json gets copied to the one we publish, and users need the latest of these.
```

### Revert
If the commit reverts a previous commit, it should begin with `revert: `, followed by the header of the reverted commit. In the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

### Type
Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing
  semi-colons, etc)
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **perf**: A code change that improves performance
* **test**: Adding missing tests or correcting existing tests
* **build**: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
* **ci**: Changes to our CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs)
* **chore**: Other changes that don't modify `src` or `test` files

### Scope
The scope could be anything specifying place of the commit change. For example
`Compiler`, `ElementInjector`, etc.

### Subject
The subject contains succinct description of the change:

* use the imperative, present tense: "change" not "changed" nor "changes"
* don't capitalize first letter
* no dot (.) at the end

### Body
Just as in the **subject**, use the imperative, present tense: "change" not "changed" nor "changes".
The body should include the motivation for the change and contrast this with previous behavior.

### Footer
The footer should contain any information about **Breaking Changes** and is also the place to
reference GitHub issues that this commit **Closes**.

**Breaking Changes** should start with the word `BREAKING CHANGE:` with a space or two newlines. The rest of the commit message is then used for this.

A detailed explanation can be found in this [document][commit-message-format].
