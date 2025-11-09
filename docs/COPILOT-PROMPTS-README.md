# GitHub Copilot Universal Prompts

This directory contains comprehensive, reusable prompts for GitHub Copilot that enable context-aware assistance for complex multi-PR projects.

## Available Prompts

### 1. EV Fleet Management Migration Prompt
**File**: `github-copilot-ev-fleet-migration-prompt.md`

**Purpose**: Migrate an EV-only Fleet Management System to support multiple fuel types (ICE, EV, Hybrid)

**Includes**:
- 18 PR specifications with complete technical details
- Architecture patterns (Backend Java/Spring Boot, Frontend React/TypeScript)
- Database migration strategies (PostgreSQL + Flyway)
- Testing requirements (Unit, Integration, E2E)
- Acceptance criteria for each PR
- Code examples and templates

**Use When**:
- Building a fleet management system
- Migrating from single-fuel to multi-fuel support
- Need structured guidance for large migrations
- Want consistent patterns across multiple PRs

## How to Use These Prompts

### Step 1: Copy the Prompt
Open the prompt file and copy the entire contents.

### Step 2: Paste into Copilot
Paste the prompt into your GitHub Copilot chat or Claude Code session **once** at the beginning of your work.

### Step 3: Reference by Description
Instead of saying "Work on PR #5", simply describe what you want:
- "Add vehicle fuel type support"
- "Update vehicle CRUD APIs"
- "Create charging validation"

Copilot will automatically:
- Identify which PR you're referring to
- Know all files that need changes
- Generate appropriate code
- Suggest tests
- Update documentation

### Step 4: Iterate
As you work through PRs, just describe the next task. The context persists throughout your session.

## Benefits of Universal Prompts

### 🎯 **Context Retention**
No need to repeat project details for each PR. Paste once, reference many times.

### 🔍 **Auto-Identification**
Describe work in natural language - Copilot knows which PR/task you mean.

### 📋 **Comprehensive Guidance**
Each PR has:
- File list
- Code patterns
- Tests needed
- Validation rules
- Acceptance criteria

### 🏗️ **Architectural Consistency**
All generated code follows consistent patterns across the entire project.

### ⚡ **Rapid Development**
Reduce setup time and context-switching between PRs.

## Creating Your Own Universal Prompt

Want to create a similar prompt for your project? Follow this structure:

### 1. Project Overview
- Tech stack
- Architecture overview
- Key technologies

### 2. PR Breakdown
For each PR/task:
- **Trigger keywords** (how to reference it)
- **Files to modify**
- **Changes needed**
- **Acceptance criteria**
- **Code examples**

### 3. Patterns
- Entity patterns
- Service patterns
- Validation patterns
- Frontend patterns

### 4. Do's and Don'ts
- Best practices
- Common pitfalls
- Performance tips

### 5. File Structure
Complete directory structure for reference

## Example Usage

```
USER: Work on vehicle fuel type support

COPILOT: I understand you want to work on PR 1: Add Vehicle Fuel Type Support.

I'll help you:
1. Create FuelType enum (ICE, EV, HYBRID)
2. Update Vehicle entity with fuel_type field
3. Add fuel tank capacity fields
4. Create Flyway migration
5. Write unit tests

Let's start with the FuelType enum...
```

## Tips for Best Results

### ✅ DO:
- Paste the entire prompt at session start
- Use natural language to describe tasks
- Reference PRs by description, not number
- Ask for specific components when needed

### ❌ DON'T:
- Break up the prompt into pieces
- Repeat context for each task
- Use PR numbers instead of descriptions
- Skip the initial prompt paste

## Prompt Maintenance

### When to Update Prompts:
- ✏️ Architecture changes
- ✏️ New patterns emerge
- ✏️ Additional PRs added
- ✏️ Technology stack updates

### Version Control:
- Keep prompts in version control
- Document changes in commit messages
- Tag versions when major updates occur

## Additional Resources

- [GitHub Copilot Documentation](https://docs.github.com/en/copilot)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)
- [Spring Boot Best Practices](https://spring.io/guides)
- [React TypeScript Patterns](https://react-typescript-cheatsheet.netlify.app/)

## Contributing

Have improvements for existing prompts or want to add new ones?

1. Follow the structure outlined above
2. Include comprehensive examples
3. Test the prompt with actual Copilot
4. Document usage instructions
5. Submit a pull request

---

**Last Updated**: 2025-11-09  
**Maintainer**: Development Team  
**License**: MIT
