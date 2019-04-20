package com.eriklievaart.tiqqer.swing;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import com.eriklievaart.toolkit.lang.api.ThrowableTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class TiqqerTableMouseListener implements MouseListener {

	private TiqqerTableModel model;
	private JTabbedPane tabs;
	private JTextArea area;

	public TiqqerTableMouseListener(TiqqerTableModel model, JTabbedPane tabs, JTextArea area) {
		this.model = model;
		this.tabs = tabs;
		this.area = area;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		JTable table = (JTable) event.getSource();
		Point point = event.getPoint();
		int row = table.rowAtPoint(point);
		if (event.getClickCount() == 2 && table.getSelectedRow() != -1) {
			showRecord(model.getRow(row));
		}
	}

	private void showRecord(LogRecord record) {
		List<String> lines = NewCollection.list();
		lines.add(Str.sub("date: $", new Date(record.getMillis())));
		lines.add(Str.sub("logger: $", record.getLoggerName()));
		lines.add(Str.sub("\nmessage:\n$", record.getMessage()));
		if (record.getThrown() != null) {
			lines.add(Str.sub("\n$", ThrowableTool.toString(record.getThrown())));
		}
		area.setText(Str.joinLines(lines));
		tabs.setSelectedIndex(1);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}
}
