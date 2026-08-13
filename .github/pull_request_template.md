> [!WARNING]
> **STOP:** All Pull Requests MUST target the `develop` branch. Any PR targeting `main` will be closed immediately.

## Description
<!-- Describe your changes in detail. What does this PR do? -->


## Linked Issue
<!-- Please link the issue this PR resolves (e.g., "Fixes #123"). All PRs should ideally be tied to an open issue. -->
Fixes #

## Type of Change
<!-- Check the appropriate box using an 'x' (e.g., [x]) -->
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Code refactoring or cleanup
- [ ] Documentation update

## Code Quality & Testing Checklist
<!-- You must check all the boxes below before submitting your PR. -->
- [ ] I have targeted the `develop` branch.
- [ ] I have tested my changes locally. <!-- using a dummy SQLite database (NOT the production database). -->
- [ ] I have formatted my code by running `mvn spotless:apply`.
- [ ] I have verified my logic and tests pass by running `mvn clean install` without any Checkstyle or JUnit errors.
- [ ] My commits follow standard naming conventions and provide clear history.

## Additional Notes (Optional)
<!-- Add any extra context, screenshots, or information the reviewer might need here. -->
