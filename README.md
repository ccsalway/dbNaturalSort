# dbNaturalSort
Calculates a string from another field value that can be used for Natural Sorting

This has the following processes:

    A. Flags each character type - letter, number, non-alphanumeric - with a sort order byte (saves padding).
    B. Converts each character into its ascii number and then converts to hex.
    C. Groups individual numbers into one number - eg abc123 would be a,b,c,123 - and converts to hex.

Example I:

    dog123 = d:64,o:6F,g:67,123:7B and thus byteSize >= 2.
    dog280 = d:64,o:6F,g:67,280:118 and thus byteSize >= 3.

A byte stores the hex value (0 to f) of a letter, number or non-alphanumeric. Since the ascii table requires two bytes (255 positions), the minimum byteSize is 2.

    2 bytes = 00 - ff  (any letter and a max number of 255)
    3 bytes = 000 - fff (any letter and a max number of 4095)
    4 bytes = 0000 - ffff (any letter and a max number of 65535)

Example II:

    "There are 1000000 spots on a dalmatian" requires a byteSize that can store the number '1000000'
    1000000 in hex is 'f4240' and thus the byteSize must be at least 5 (f4240 has 5 characters)

The dbColumn size to store the NaturalSortString is calculated as: `originalStringColumnSize x byteSize + 1` (The extra '1' is a marker for String type - Letter, Number, Symbol). Thus, if the originalStringColumn is varchar(32) and the byteSize is 5: 
`NaturalSortStringColumnSize = 32 x 5 + 1` = varchar(161)

**The byteSize must be the same for all NaturalSortStrings created in the same table.**

If you need to change the byteSize (for instance, to accommodate larger numbers), you will need to recalculate the NaturalSortString for each existing row using the new byteSize.
