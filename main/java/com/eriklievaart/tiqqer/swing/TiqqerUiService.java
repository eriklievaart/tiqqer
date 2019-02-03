package com.eriklievaart.tiqqer.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.eriklievaart.tiqqer.api.TiqqerService;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.builder.JFrameBuilder;

public class TiqqerUiService implements TiqqerService {
	private static final int UI_BUFFER = 1_000;
	private static final int LOG_BUFFER = 1_000_000;

	private final List<LogRecord> records = NewCollection.concurrentList();
	private final JFrame frame = new JFrameBuilder("tiqqer").title("tiqqer").create();
	private final TiqqerTableModel model = new TiqqerTableModel();
	private final JTable table = new JTable(model);
	private final JPanel panel = new JPanel(new GridLayout(1, 0));
	private final JComboBox<LevelType> levelBox = new JComboBox<>(LevelType.values());
	private final JTextField loggerField = new JTextField();
	private final JTextField messageField = new JTextField();
	private final JButton updateButton = new JButton("update");
	private final JButton clearButton = new JButton("clear");
	private final AtomicReference<Predicate<LogRecord>> predicate = new AtomicReference<>(r -> true);

	@Override
	public void publish(LogRecord record) {
		records.add(record);
		if (records.size() > LOG_BUFFER) {
			records.remove(0);
		}
		updateRows();
	}

	public void show() {
		SwingThread.invokeLater(() -> {
			clearButton.addActionListener(ae -> clear());
			frame.getContentPane().add(clearButton, BorderLayout.NORTH);

			table.getColumnModel().getColumn(0).setMaxWidth(60);
			table.setFillsViewportHeight(true);
			frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

			configureButtonPanel();
			frame.getContentPane().add(panel, BorderLayout.SOUTH);
			frame.getRootPane().setDefaultButton(updateButton);

			frame.setBounds(0, 0, 800, 600);
			frame.setVisible(true);
		});
	}

	private void clear() {
		records.clear();
		updateRows();
	}

	private void configureButtonPanel() {
		levelBox.setSelectedItem(LevelType.TRACE);
		addBorderedField("minimum level", levelBox);
		addBorderedField("logger", loggerField);
		addBorderedField("message", messageField);
		updateButton.addActionListener(ae -> updatePredicate());
		panel.add(updateButton);
	}

	private void updatePredicate() {
		List<Predicate<LogRecord>> predicates = NewCollection.list();
		String message = messageField.getText().trim();
		if (Str.notBlank(message)) {
			predicates.add(record -> WildcardTool.match(message, record.getMessage()));
		}
		String logger = loggerField.getText().trim();
		if (Str.notBlank(logger)) {
			predicates.add(record -> WildcardTool.match(logger, record.getLoggerName()));
		}
		LevelType level = (LevelType) levelBox.getSelectedItem();
		if (level != LevelType.TRACE) {
			predicates.add(record -> level.isLoggable(LevelType.fromJulLevel(record.getLevel())));
		}
		if (predicates.isEmpty()) {
			predicate.set(r -> true);
		} else {
			predicate.set(r -> predicates.stream().allMatch(p -> p.test(r)));
		}
		updateRows();
	}

	private void addBorderedField(String label, JComponent field) {
		TitledBorder border = BorderFactory.createTitledBorder(label);
		field.setBorder(border);
		panel.add(field);
	}

	private void updateRows() {
		List<LogRecord> data = getLines();
		SwingThread.invokeLater(() -> {
			model.update(data);
			if (data.size() > 0) {
				table.changeSelection(table.getRowCount() - 1, 0, false, false);
			}
		});
	}

	private List<LogRecord> getLines() {
		List<LogRecord> filtered = records.stream().filter(predicate.get()).collect(Collectors.toList());
		int count = filtered.size();
		if (count < UI_BUFFER) {
			return FromCollection.toList(filtered);
		}
		return ListTool.subList(filtered, count - UI_BUFFER, count - 1);
	}

	public void shutdown() {
		frame.dispose();
	}
}
