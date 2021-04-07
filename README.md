# design-assign-1
For this assignment, I attempted problem 8, whose premise is as follows

> You work as a Youtuber who makes how-to videos on different kinds of braids. In the comments of your videos, you see your fans are complaining that they see pictures of braids online that they want to replicate, but they don’t know the name of the braid. Thus they have a hard time finding the right video of yours to watch in order to learn the steps. You want to create a software that lets people go between pictures of braids and your instructional video. If you upload a picture, the program will return the name of the braid and the video URL, and if you input the name of a braid, it will return the video URL and a picture of the braid (assuming that all braids of a certain kind can be processed to have the same image).

Most of the design decisions have been explained in the code via comments. Here I give a high-level overview:

1. A single entity that combines three objects (url, picture, name) together called `Aggregate` was introduced. This allows us to update any of the three fields and have the changes visible to all access points.
2. Three hash maps that map urls, pictures, and names to the `Aggregate` object were used. Hashmaps were used because of their constant time lookup operation.
3. Specifically `WeakHashMap` was used as my implementation of hash map. This design choice was driven by the fact that we are essentially maintaining a cache, and once a strong reference to name, url, or picture is gone, the program is free to recollect the memory allocated for that object. While it is true that a regular `HashMap` could have been used, manually keeping track of map entries could be error-prone.