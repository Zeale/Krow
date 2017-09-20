package krow.guis.math_module;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabGroup extends ArrayList<Tab> {

	private static final long serialVersionUID = 1L;

	public TabGroup show(TabPane pane) {
		TabGroup group = new TabGroup();

		if (!pane.getTabs().isEmpty())
			group.addAll(pane.getTabs());

		pane.getTabs().clear();
		pane.getTabs().addAll(this);

		return group;

	}

	public TabGroup() {
		// TODO Auto-generated constructor stub
	}

	public TabGroup(Collection<? extends Tab> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public TabGroup(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public TabGroup(Tab... tabs) {
		addAll(tabs);
	}

	public void addAll(Tab... tabs) {
		for (Tab t : tabs)
			add(t);
	}

	public boolean isShowing(TabPane pane) {
		if (size() != pane.getTabs().size())
			return false;
		for (int i = 0; i < size(); i++)
			if (pane.getTabs().get(i) != get(i))
				return false;
		return true;

	}

}
