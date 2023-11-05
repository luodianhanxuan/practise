import org.openjdk.jol.info.ClassLayout;

public class EmptyTest {
    public static void main(String[] args) {
        Empty empty = new Empty();
        ClassLayout classLayout = ClassLayout.parseInstance(empty);
        String printable = classLayout.toPrintable();
        System.out.println(printable);

        long l = classLayout.instanceSize();

        System.out.println(l);
    }
}
