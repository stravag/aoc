# Advent of Code

Attempt at solving the annual [Advent of Code](https://adventofcode.com) puzzle fun.

# Git-Crypt

The issue
to [not publish your input files](https://www.reddit.com/r/adventofcode/comments/zh2hk0/2022friendly_reminder_dont_commit_your_input/)
came up on Reddit.

There are multiple ways to honor this request. I chose encrypting the input files before pushing them to a public repo.
For this I'm using [git-crypt](https://github.com/AGWA/git-crypt), kudos to [shrugalic](https://github.com/shrugalic/advent_of_code/blob/main/git-crypt.md)
for showing me how to set it up.

## Steps to decrypt puzzle files

### Install tools

```shell
brew install git-crypt
brew install 1password-cli
```

### Get key from 1Password and decrypt

```shell
op read --out-file aoc_git-crypt2.key "op://Personal/Advent of Code Git-Crypt Key/aoc_git-crypt.key"
git-crypt unlock aoc_git-crypt.key
```

It goes without saying that future me should not commit the key file.

## Additional Infos

Git-Crypt configuration see: `.gitattributes` file

Helpful `git-crypt` commands

```shell
git-crypt status # check what will be encrypted
git-crypt status -e # show only files that are or should be encrypted
```

The following operations require the working directory to be clean

```shell
git-crypt lock # Lock files in repo (happens transparently on push to remote)
git-crypt unlock ../advent_of_code_git-crypt.key # Unlock encrypted files in local repo (also happens transparently on pull)
```
