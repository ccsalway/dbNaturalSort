# dbNaturalSort
Calculates a string from another field value that can be used for Natural Sorting

A byte stores the hex value (0 to f) of a letter or number.
Since a letter is two bytes, the minimum byteSize is 2.

2 bytes = 00 - ff  (max number is 255)
3 bytes = 000 - fff (max number is 4095)
4 bytes = 0000 - ffff (max number is 65535)

For example:
dog123 = 64,6F,67,7B and thus byteSize >= 2.
dog280 = 64,6F,67,118 and thus byteSize >= 3.

For example:
The String, "There are 1000000 spots on a dalmatian" would require a byteSize that can
store the number '1000000' which in hex is 'f4240' and thus the byteSize must be at least 5

The dbColumn size to store the NaturalSortString is calculated as:
> originalStringColumnSize x byteSize + 1
The extra '1' is a marker for String type - Letter, Number, Symbol
Thus, if the originalStringColumn is varchar(32) and the byteSize is 5:> NaturalSortStringColumnSize = 32 x 5 + 1 = varchar(161)

The byteSize must be the same for all NaturalSortStrings created in the same table.
If you need to change the byteSize (for instance, to accommodate larger numbers), you will
need to recalculate the NaturalSortString for each existing row using the new byteSize.
