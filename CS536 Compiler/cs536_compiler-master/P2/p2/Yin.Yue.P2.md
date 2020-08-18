# Programming Assignment 2
## Commands
- Invoke the program
    ```    
    $ make
    ```

- Run all tests
    ```
    $ make test
    ```

- clean all test output
    ```
    $ make cleantest
    ```

## Test case description  
Each test corresponds to a method in `P2.java`.

| Test Target                        | Files                                   |
|------------------------------------|-----------------------------------------|
| all reserved words                 | allReservedWords.[in, out]              |
| all symbols                        | allSymbols.[in, out]                    |
| good strings                       | goodStrings.[in, out]                   |
| unterminated strings               | unterminatedStrings.[in, out, expected] |
| bad escape strings                 | badEscapeStrings[in, out, expected]     |
| unterminated strings w/ bad escape | veryBadStrings.[in, out, expected]      |
| Strings from all categories        | mixedStrings.[in, out, expected]        |
| comments                           | comments.[in, out]                      |
| line num, char num, whitespace     | fib.[in, out]                           |
| identifiers                        | identifiers.[in, out]                   |
| integer literals                   | intLiterals.[in, out, expected]         |