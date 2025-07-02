# Tentai Show

This is code to solve [Tentai Show](https://www.nikoli.co.jp/en/puzzles/tentai_show/) puzzles.

To run it you need to have Java installed, then to solve the game in the file game1.txt, do the following:

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

It gives output like the following, showing first the star map. Then a map showing 
how each square is associated with a star (the stars are named a0, b0, etc). Finally the
puzzle is rendered as an "image" with the squares associated with a black star shown in 
black (this uses ANSI terminal colours to work).

```
Solving:

·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| ○ |   ●   | ○ |   | ○ |   ●   |   ○   |
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒○‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
|   |   |   |   |   |   |   |   |   ●   |
·‒●‒·‒‒‒○‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒○‒·‒○‒·‒‒‒·‒‒‒·
|   |   |   |   | ● |   |   |   | ● |   |
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
|   |   ●   |   |   |   |   ●   |   ○   |
·‒○‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
|   |   |   | ● |   | ● |   |   |   | ○ |
·‒‒‒·‒○‒·‒‒‒·‒‒‒·‒○‒·‒‒‒·‒‒‒·‒○‒·‒‒‒·‒‒‒·
|   |   |   |   |   |   |   |   |   |   |
·‒‒‒·‒‒‒·‒●‒·‒○‒·‒‒‒·‒○‒·‒●‒·‒‒‒·‒‒‒·‒‒‒·
|   |   |   |   |   |   |   |   | ○ |   |
·‒‒‒·‒○‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| ● |   |   |   | ● |   | ○ |   | ● | ○ |
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| ○ |   ●   |   |   |   |   ●   |   |   |
·‒‒‒·‒‒‒·‒‒‒·‒‒‒○‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒○‒‒‒·
|   ○   |   |   |   |   | ○ |   |   |   |
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·

Result:

·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| a0| b0| b0| c0| d0| e0| f0| f0| g0| g0|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| h0| i0| i0| m0| d0| m0| j0| k0| l0| l0|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| h0| i0| i0| m0| m0| m0| j0| k0| n0| r0|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| o0| p0| p0| m0| u0| m0| q0| q0| r0| r0|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| o0| s0| s0| t0| u0| v0| w0| w0| r0| x0|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| s0| s0| y0| z0| u0| a1| b1| w0| w0| d1|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| c1| c1| y0| z0| u0| a1| b1| d1| d1| d1|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| e1| c1| c1| f1| f1| f1| g1| d1| h1| i1|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| j1| k1| k1| l1| l1| l1| m1| m1| n1| n1|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·
| o1| o1| l1| l1| l1| p1| p1| p1| n1| n1|
·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·‒‒‒·

```

![Solution Image](tentai_image.png?raw=true)

The method used is :
- First of all fill in the "gimmes" which are squares directly next to a star.
- Then repeatedly look for squares that can only be associated with one star, by looking at stars that can
  be reached and where the "mirror" square is also available.
- Finally a recursive search is operated trying each choice of star in turn looking for a solution.
