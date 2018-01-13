package kröw.data.protection;

/**
 * <p>
 * This class will be used to demonstrate {@link Protection}'s
 * {@link Protection#getDomain()} method for this commit.
 * <p>
 * This class will be removed by the next commit.
 * 
 * @author Zeale
 *
 */
public final class Demonstration {
	private Demonstration() {
	}

	private static class Test1 {
		{
			System.out.println(Protection.getDomain());
		}
	}

	private static class Test2 {
	}

	private class Test3 {
		{
			System.out.println(Protection.getDomain());
		}
	}

	private class Test4 {
	}

	public static void main(String[] args) {

		System.out.println("\tNested classes (static)");
		System.out.println("getDomain() called by class constructor:");
		new Test1();

		System.out.println();
		System.out.println("getDomain() called by anonymous class in main method:");
		new Test2() {
			{
				System.out.println(Protection.getDomain());
			}
		};

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("\tInner classes (non-static)");

		Demonstration innerClassWrapper = new Demonstration();

		System.out.println("getDomain() called by class constructor:");
		innerClassWrapper.new Test3();

		System.out.println();
		System.out.println("getDomain() called by anonymous class in main method:");
		innerClassWrapper.new Test4() {
			{
				System.out.println(Protection.getDomain());
			}
		};

	}

}
