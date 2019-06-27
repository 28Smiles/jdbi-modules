# v2.0.1
 - Add function to module meta

# v2.0.0
 - Fix object persistent single bean mapping methods
 - Remove extraneous generic type of module meta method
 - Remove flawed methods from public API that can cause unintended behavior

BREAKING CHANGE

Use newly created methods of the ModuleMeta interface when mapping single beans, it is now required to provide a getter instead of just a type. These changes will ensure that contents of a bean are correctly mapped across multiple inner instances.


# v1.1.2

- Fix bug in string replacement (Use antlr lexer)

# v1.1.1

- Fix bug in string replacement

# v1.1.0

- Add equals and hashcode to query modifiers
- Add spaces after comma of concatenation in StructuredSqlGenerator

# v1.0.1

- ModuleMeta's callSubmodule can now apply a function on the accessed elements
- Remove unnecessary space at the end of a query created with the StucturedQueryGenerator
- Fix an issue where the function to apply on the added and accessed elements where not applied if the fallback was used to map

# v1.0.0

- Allow Postgres JSON expression for prefix function
- Use a normal HashMap in the Module instead of an ConcurrentHashMap.
- Breaking Change: Remove the get prefix of all methods of StructuredSqlGenerator

# v0.3.0

- Refactor ModuleMeta to use less generics
- Add builder pattern to ModuleMeta
- Add type filter for Collector

# v0.2.1

- Fix PrefixGenerator overflow
- Fix remove spaces in the generated SQL of StructuredSQLGenerator

# v0.2.0

- Fix a bug throwing exception if no element was added

# v0.1.3

- Added Cte's to structured sql generator

# v0.1.2

- Added default methods to the StructuredSqlGenerator interface

# v0.1.1

- Fixed an error in the StructuredSqlGenerator implementation

# v0.1.0

- Added method to retrieve store from ModuleMeta
- Added wildcard to modules row mappers
