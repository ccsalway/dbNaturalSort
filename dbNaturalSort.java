/**
 * Creates a string that allows natural sorting in a SQL database
 * eg, 0 1 1a 2 3 3a 10 100 a a1 a1a1 b
 */
public class NaturalSortString {

    private String inStr;
    private int byteSize;
    private StringBuilder out = new StringBuilder();

    /**
     * A byte stores the hex value (0 to f) of a letter or number.
     * Since a letter is two bytes, the minimum byteSize is 2.
     * <p>
     * 2 bytes = 00 - ff  (max number is 255)
     * 3 bytes = 000 - fff (max number is 4095)
     * 4 bytes = 0000 - ffff (max number is 65535)
     * <p>
     * For example:
     * dog123 = 64,6F,67,7B and thus byteSize >= 2.
     * dog280 = 64,6F,67,118 and thus byteSize >= 3.
     * <p>
     * For example:
     * The String, "There are 1000000 spots on a dalmatian" would require a byteSize that can
     * store the number '1000000' which in hex is 'f4240' and thus the byteSize must be at least 5
     * <p>
     * The dbColumn size to store the NaturalSortString is calculated as:
     * > originalStringColumnSize x byteSize + 1
     * The extra '1' is a marker for String type - Letter, Number, Symbol
     * Thus, if the originalStringColumn is varchar(32) and the byteSize is 5:
     * > NaturalSortStringColumnSize = 32 x 5 + 1 = varchar(161)
     * <p>
     * The byteSize must be the same for all NaturalSortStrings created in the same table.
     * If you need to change the byteSize (for instance, to accommodate larger numbers), you will
     * need to recalculate the NaturalSortString for each existing row using the new byteSize.
     *
     * @param str      String to create a natural sort string from
     * @param byteSize Per character storage byte size (minimum 2)
     * @throws Exception See the error description thrown
     */
    public NaturalSortString(String str, int byteSize) throws Exception {
        if (str == null || str.isEmpty()) return;
        this.inStr = str;
        this.byteSize = Math.max(2, byteSize);  // minimum of 2 bytes to hold a character
        setStringType();
        iterateString();
    }

    private void setStringType() {
        char firstchar = inStr.toLowerCase().subSequence(0, 1).charAt(0);
        if (Character.isLetter(firstchar))     // letters third
            out.append(3);
        else if (Character.isDigit(firstchar)) // numbers second
            out.append(2);
        else                                   // non-alphanumeric first
            out.append(1);
    }

    private void iterateString() throws Exception {
        StringBuilder n = new StringBuilder();
        for (char c : inStr.toLowerCase().toCharArray()) { // lowercase for CASE INSENSITIVE sorting
            if (Character.isDigit(c)) {
                // group numbers
                n.append(c);
                continue;
            }
            if (n.length() > 0) {
                addInteger(n.toString());
                n = new StringBuilder();
            }
            addCharacter(c);
        }
        if (n.length() > 0) {
            addInteger(n.toString());
        }
    }

    private void addInteger(String s) throws Exception {
        int i = Integer.parseInt(s);
        if (i >= (Math.pow(16, byteSize)))
            throw new Exception("naturalsort_bytesize_exceeded");
        out.append(padZerosLeft(Integer.toHexString(i), byteSize));
    }

    private void addCharacter(char c) {
        //TODO: Add rest of accented characters
        if (c >= 224 && c <= 229) // set accented a to a
            c = 'a';
        else if (c >= 232 && c <= 235) // set accented e to e
            c = 'e';
        else if (c >= 236 && c <= 239) // set accented i to i
            c = 'i';
        else if (c >= 242 && c <= 246) // set accented o to o
            c = 'o';
        else if (c >= 249 && c <= 252) // set accented u to u
            c = 'u';
        else if (c >= 253 && c <= 255) // set accented y to y
            c = 'y';

        out.append(padZerosLeft(Integer.toHexString(c), byteSize));
    }
    
    private String padZerosLeft(String s, int n) {
        if (n - s.length() == 0)
            return s;

        return String.format("%0" + (n - s.length()) + "d%s", 0, s);
    }
    
    @Override
    public String toString() {
        return out.toString();
    }
}

