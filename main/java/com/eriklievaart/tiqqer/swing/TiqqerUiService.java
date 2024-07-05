package com.eriklievaart.tiqqer.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.eriklievaart.tiqqer.agent.api.TiqqerService;
import com.eriklievaart.tiqqer.swing.api.TiqqerFrame;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.builder.JFrameBuilder;

public class TiqqerUiService implements TiqqerService, TiqqerFrame {
	private static final int UI_BUFFER = 1_000;
	private static final int LOG_BUFFER = 1_000_000;

	private final List<LogRecord> records = NewCollection.concurrentList();
	private final JFrame frame = new JFrameBuilder("tiqqer").title("tiqqer").create();
	private final JTabbedPane tabs = new JTabbedPane();

	private final TiqqerTableModel model = new TiqqerTableModel();
	private final JTable table = new JTable(model);
	private final JPanel overviewPanel = new JPanel(new BorderLayout());
	private final JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
	private final JComboBox<LevelType> levelBox = new JComboBox<>(LevelType.values());
	private final JTextField loggerField = new JTextField();
	private final JTextField messageField = new JTextField();
	private final JButton clearButton = new JButton("clear");
	private final AtomicReference<Predicate<LogRecord>> predicate = new AtomicReference<>(r -> true);

	private final JPanel detailPanel = new JPanel(new BorderLayout());
	private final JTextArea detailArea = new JTextArea();
	private final TiqqerTableMouseListener listener = new TiqqerTableMouseListener(model, tabs, detailArea);

	@Override
	public void publish(LogRecord record) {
		records.add(record);
		if (records.size() > LOG_BUFFER) {
			records.remove(0);
		}
		updateRows();
	}

	@Override
	public void show() {
		SwingThread.invokeLater(() -> {
			initOverviewPanel();
			initDetailPanel();

			frame.getContentPane().add(tabs, BorderLayout.CENTER);
			tabs.addTab("overview", overviewPanel);
			tabs.addTab("details", detailPanel);

			frame.setBounds(0, 0, 800, 600);
			frame.setVisible(true);
		});
	}

	private void initDetailPanel() {
		detailPanel.add(new JScrollPane(detailArea));
		detailArea.setBackground(Color.BLACK);
		detailArea.setForeground(Color.GRAY);
		detailArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					tabs.setSelectedIndex(0);
				}
			}
		});
	}

	private void initOverviewPanel() {
		initButtonPanel();

		table.setBackground(Color.BLACK);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.setFillsViewportHeight(true);
		table.addMouseListener(listener);
		table.setDefaultRenderer(Object.class, new ColorRenderer(model));

		overviewPanel.add(buttonPanel, BorderLayout.NORTH);
		overviewPanel.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void clear() {
		records.clear();
		updateRows();
	}

	private void initButtonPanel() {
		levelBox.setSelectedItem(LevelType.TRACE);
		levelBox.addActionListener(e -> updatePredicate());
		loggerField.addActionListener(e -> updatePredicate());
		messageField.addActionListener(e -> updatePredicate());

		addBorderedField("minimum level", levelBox);
		addBorderedField("logger", loggerField);
		addBorderedField("message", messageField);

		clearButton.addActionListener(ae -> clear());
		buttonPanel.add(clearButton);
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
		buttonPanel.add(field);
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
