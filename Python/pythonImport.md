### 0. Preliminaries
Two types of modules in Python: code module and package module. A code module is any file that contains python executable code. A package module is a directory that contains other modules (either code modules or package modules). The most common type of code modules are `*.py` files while the most common type of package modules are directories containing an `__init__.py` file. So *packages are modules too*. Reference: [Stack Overflow](https://stackoverflow.com/a/62923810/9057530).

`-m` option [documentation](https://docs.python.org/3/using/cmdline.html#cmdoption-m): "Search `sys.path` for the named module and execute its contents as the `__main__` module". In my understanding, `-m` allows you to run a script without specifying the full path. Reference: [Stack Overflow](https://stackoverflow.com/a/46320032/9057530).

### 1. Import basis
For statement
```python
import abc
```
Python looks at the following in order:
- `sys.modules`, a cache of modules previously imported
- built-in modules that come pre-installed with Python
- `sys.path`, which includes the current directory. The current directory is searched first.

`sys.modules` and built-in modules contain builtin modules like `sys`, `itertools`.  

`sys.path` includes the current directory (`''`), and paths to package you installed with `pip3`. 

If no name is found, you get `ModuleNotFound` exception.

```
$ python3
Python 3.8.2 (default, Jul 16 2020, 14:00:26) 
[GCC 9.3.0] on linux
Type "help", "copyright", "credits" or "license" for more information.
>>> import sys
>>> print(len(sys.modules))
469
>>> print(sys.path)
['', '/usr/lib/python38.zip', '/usr/lib/python3.8', '/usr/lib/python3.8/lib-dynload', '/home/yy0125/.local/lib/python3.8/site-packages', '/usr/local/lib/python3.8/dist-packages', '/usr/lib/python3/dist-packages']
```

Importing a package essentially imports the package's `__init__.py` file as a module.

### 2. Import syntax
```python
# Type1: import directly
import foobar

# Type2: import from another package/module
from foo import bar

# You can rename the imported resource
import bar as baz
```

### 3. Absolute imports
An absolute import specifies the resource using its full path from the project's root folder.

Check `importExample/absolute.py`.

### 4. Relative import
A relative import specifies the resource to be imported relative to the current location - the location where the import statement is. For relative import, there's at least one `.` in each import statement. 

Check `importExample/relative.py`.

If you are using relative import, you should use `python -m` to avoid import errors.

### 5. Precedence of Module and Package
Stack Overflow [Reference](https://stackoverflow.com/a/4092446/9057530)

The reason is that, when ordered, the directory `foo` comes before `foo.py`. Note that in this case, the directory doesn't end with a `/`. You can verify this by printing out `sys.path`. 

This explains the possible [confusing situation](https://stackoverflow.com/a/64745978/9057530). Consider you use `pip3` to install a package `foo`, which contains a `bar` module. So this means you can execute `python3 -m foo.bar` from any directory. On the other hand, you have a directory structure like this:
```
src
|
+-- foo
    |
    +-- __init__.py
    |
    +-- bar.py
```
You are at `src/`. When you run `python -m foo.bar`, you are running the `bar.py`, instead of the installed module. However, if you are calling `python -m foo.bar` from any other directory, you are using the installed module. This can cause confusion. 


### 6. Add current directory to `sys.path`
As mentioned earlier, Python would add current directory to `sys.path` before searching on it. From [this post](https://courses.cs.washington.edu/courses/cse140/13wi/file-interaction.html), `os.getcwd()` is the path you are when your are running the command.  

```console
yy0125@XPS13YY:/mnt/c/Users/yy0125/Desktop/blogs/Python$ python -c "import sys; print(sys.path)"
['', '/usr/lib/python38.zip', '/usr/lib/python3.8', '/usr/lib/python3.8/lib-dynload', '/home/yy0125/.local/lib/python3.8/site-packages', '/usr/local/lib/python3.8/dist-packages', '/usr/lib/python3/dist-packages']

yy0125@XPS13YY:/mnt/c/Users/yy0125/Desktop/blogs/Python$ python -m importExample.printPath
/mnt/c/Users/yy0125/Desktop/blogs/Python
['/mnt/c/Users/yy0125/Desktop/blogs/Python', '/usr/lib/python38.zip', '/usr/lib/python3.8', '/usr/lib/python3.8/lib-dynload', '/home/yy0125/.local/lib/python3.8/site-packages', '/usr/local/lib/python3.8/dist-packages', '/usr/lib/python3/dist-packages']

yy0125@XPS13YY:/mnt/c/Users/yy0125/Desktop/blogs/Python$ python importExample/printPath.py 
/mnt/c/Users/yy0125/Desktop/blogs/Python
['/mnt/c/Users/yy0125/Desktop/blogs/Python/importExample', '/usr/lib/python38.zip', '/usr/lib/python3.8', '/usr/lib/python3.8/lib-dynload', '/home/yy0125/.local/lib/python3.8/site-packages', '/usr/local/lib/python3.8/dist-packages', '/usr/lib/python3/dist-packages']
```
Observe that using `-m` cause different behavior than running the script directly. I posted a [question](https://stackoverflow.com/questions/64746046/how-python-adds-current-directory-to-sys-path) on Stack Overflow. 
