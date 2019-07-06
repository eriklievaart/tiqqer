package com.eriklievaart.tiqqer.swing;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.table.AbstractTableModel;

import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class TiqqerTableModel extends AbstractTableModel {

	private List<LogRecord> data = NewCollection.list();

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {

		case 0:
			return "level";
		case 1:
			return "logger";
		case 2:
			return "message";
		}
		throw new FormattedException("there is no column %", col);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		LogRecord record = data.get(row);
		switch (col) {

		case 0:
			return getLevelString(record.getLevel());
		case 1:
			return record.getLoggerName();
		case 2:
			return record.getMessage().replaceAll("\\s++", " ");
		}
		throw new FormattedException("there is no column %", col);
	}

	private String getLevelString(Level level) {
		return LevelType.fromJulLevel(level).toString();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		throw new UnsupportedOperationException();
	}

	public void update(List<LogRecord> records) {
		this.data = records;
		fireTableDataChanged();
	}

	public LogRecord getRow(int row) {
		return data.get(row);
	}
}
