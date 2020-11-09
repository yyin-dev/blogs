# Note the difference between os.getcwd() and sys.path[0]
# os.cwd(): /mnt/c/Users/yy0125/Desktop/blogs
# sys.path[0]: /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample
# Current working directory is where you call "python ...". However, when Python
# adds "current directory" to sys.path, it's the path where the file resides.
#
# $ python Python/importExample/absolute.py 
# /mnt/c/Users/yy0125/Desktop/blogs
# ['/mnt/c/Users/yy0125/Desktop/blogs/Python/importExample', '/usr/lib/python38.zip', '/usr/lib/python3.8', '/usr/lib/python3.8/lib-dynload', '/home/yy0125/.local/lib/python3.8/site-packages', '/usr/local/lib/python3.8/dist-packages', '/usr/lib/python3/dist-packages']
# /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample/package1/module1.py
# /mnt/c/Users/yy0125/Desktop/blogs/Python/importExample/package2/subpackage1/module5.py

import os
print(os.getcwd())

import sys
print(sys.path)

# from package1 import module1
# print(module1.__file__)

# from package2.subpackage1 import module5
# print(module5.__file__)