package kröw.data.protection;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import kröw.data.protection.DomainAccess.AccessBox;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(AccessBox.class)
public @interface DomainAccess {

	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	@interface AccessBox {
		DomainAccess[] value();
	}

	public static enum AccessOption {
		INHERITED, UP_INHERITED, NONE;
	}

	public static enum SingleAccessOption {
		PUBLIC, PRIVATE;
	}

	Class<?>[] allowedAccessors() default Void.class;

	AccessOption[] options() default AccessOption.NONE;

	SingleAccessOption overallOption() default SingleAccessOption.PRIVATE;
}