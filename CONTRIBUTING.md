# Contributing

This is an incomplete list of things to take care while contributing.


## Commits
- Don't touch files or pieces non-related to the commit message, create a different commit instead.
- Keep the commits simple to make the reviews easy.
- Merge commits will be rejected, use rebase instead, run `git config pull.rebase true` after cloning the repository to rebase automatically.
- Every commit should have working code with all tests passing.
- Every commit should include tests unless it is not practical.

## Code style
- We use [scalafmt](https://scalameta.org/scalafmt/) to format the code automatically, follow the [IntelliJ setup for scalafmt](https://scalameta.org/scalafmt/docs/installation.html#intellij).

## Pull requests
- The pull requests should go to the `master` branch.
- Make sure to test the native builds to avoid this [issue](https://github.com/wiringbits/my-photo-timeline/issues/1).

## Environment
It is simpler to use the recommended developer environment.

- IntelliJ with the Scala plugin, configured to format-on-save.
