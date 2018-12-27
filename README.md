# Tentai Show

This is code to solve [Tentai Show](http://nikoli.co.jp/en/puzzles/astronomical_show.html) puzzles.

To run it to solve the game in the file game1.txt:

```
> ./sbt "runMain uk.me.chrs.tentaishow.Game" < game1.txt
```

The file format has one line per row in the puzzle, with one character per square. Upper case letters 
represent black stars and lower case letters represent white stars. The characters are:
- A dot `.` represents a square with no stars
- A `c` represents a square with a star in the centre
- A `b` represents a square with a star on the line below it
- An `r` represents a square with a star on the line to the right of it 
- A `d` represents a square with a star on the corner below and right of it