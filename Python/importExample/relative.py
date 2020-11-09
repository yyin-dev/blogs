# If you are using relative import
# Always run as `python3 -m` to avoid import error.
#
# $ python -m importExample.relative
# /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample/package1/module1.py
# /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample/package2/module3.py
# /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample/package2/subpackage1/module5.py
#
# $ python importExample/relative.py 
# Traceback (most recent call last):
#   File "importExample/relative.py", line 1, in <module>
#     from .package1 import module1
# ImportError: attempted relative import with no known parent package

import os
print(os.getcwd())

import sys
print(sys.path)


# from .package1 import module1
# print(module1.__file__)

# from .package2 import module3
# print(module3.__file__)

# from .package2.subpackage1 import module5
# print(module5.__file__)