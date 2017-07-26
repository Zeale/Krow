package kröw.libs.mindset.tables;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import kröw.libs.mindset.Construct;

public interface TableViewable {
	/**
	 * <p>
	 * This class is used to generate a CellValueFactory for a
	 * {@link TableCell}.
	 * <p>
	 * This class takes in a {@link String} as a key. The key will determine the
	 * value that this factory will return.
	 * <p>
	 * Individual classes that implement the {@link TableViewable} interface are
	 * responsible for writing their {@link TableViewable#getProperty(String)}
	 * method. This method takes in the key you give and builds an
	 * {@link ObservableValue} that represents the value which corresponds to
	 * the key you specify. A sufficient instantiation of this class on a
	 * {@link TableColumn} with the generic types
	 * <code><Construct, String></code> would be as follows:
	 *
	 * <pre>
	 * TableColumn<Construct, String> tc = new TableColumn<>();
	 * tc.setCellValueFactory(new MindsetObject.MindsetObjectTableViewCellValueFactory<>("Name"));
	 * </pre>
	 *
	 * <p>
	 * The {@link Construct} class defines the "Name" key to return an
	 * {@link ObservableValue} which represents the name of a {@link Construct}.
	 *
	 * <p>
	 * The above code will make the {@link TableColumn}, {@code tc}, show each
	 * {@link Construct}'s name. Here's a styled, snipped, image of what the
	 * {@link TableColumn} might look like if its {@link TableView} has the
	 * values <code>"Zeale", "Unnamed", "Wolf"</code> in no specific order:
	 *
	 * <pre>
	 * <img src="doc-files/WorkingCellValueFactoryOutput.PNG"></img>
	 * </pre>
	 *
	 * <p>
	 * The {@link TableColumn} successfully gets populated with
	 * {@link Construct} names (and only the names). For a full list of the
	 * property values of a class, see the class's documentation. (The
	 * {@link Construct} class, for example, documents key-value pairs in the
	 * class header.)
	 *
	 * <p>
	 * {@code tc} is a {@link TableColumn} from inside a {@link TableView}. The
	 * {@link TableView} was constructed like so:
	 *
	 * <pre>
	 * TableView<Construct> table = new TableView<>();
	 * </pre>
	 *
	 * @author Zeale
	 *
	 * @param <S>
	 *            The type of data stored in the {@link TableView}.
	 * @param <T>
	 *            The type of {@link ObservableValue} data to be given by this
	 *            factory.
	 */
	class TableViewCellValueFactory<S extends TableViewable, T>
			implements Callback<CellDataFeatures<S, T>, ObservableValue<T>> {
		private final String key;

		public TableViewCellValueFactory(final String key) {
			this.key = key;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObservableValue<T> call(final CellDataFeatures<S, T> param) {
			return (ObservableValue<T>) param.getValue().getProperty(key);
		}

		protected String getKey() {
			return key;
		}

	}

	ObservableValue<?> getProperty(String key);
}
