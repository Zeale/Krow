package kr�w.data.common;

public interface Convertible<CT> {
	CT convertTo();
	void convertFrom(CT convertedObject);
}
