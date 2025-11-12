# Committing Generated Files

The generated Spring Boot modulith lives in the `chemical-list-api/` directory at the root of this repository. Run the commands below from the repository root (`chemical-lists/`).

1. Ensure you are on the correct branch (for example, `work`).
   ```bash
   git status -sb
   ```
2. Stage the new project directory and any updated files.
   ```bash
   git add chemical-list-api docs/COMMITTING.md
   ```
   If you touched additional files, add them as well.

3. Review the staged changes before committing.
   ```bash
   git status
   git diff --staged
   ```

4. Create a commit with a descriptive message.
   ```bash
   git commit -m "Add Spring Boot modulith project skeleton"
   ```

5. Push the commit to the remote repository.
   ```bash
   git push origin work
   ```

After pushing, open a pull request that summarizes the changes and links to any relevant context.
