import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public abstract class LabelledFieldPanel extends JPanel{
	protected JTextField textField;
	
	public LabelledFieldPanel(String label, JTextField field) {
		super();
		JLabel fieldLabel = new JLabel(label);
		textField = field;
		add(fieldLabel);
		add(textField);
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
	}
	public abstract String getValue();
}

@SuppressWarnings("serial")
class LabelledTextFieldPanel extends LabelledFieldPanel{
	public LabelledTextFieldPanel(String label,int fieldSize) {
		super(label, new JTextField(fieldSize));
	}
	public LabelledTextFieldPanel(String label,int fieldSize,String defaultString) {
		super(label, new JTextField(defaultString,fieldSize));
	}
	public void setEditable(boolean editable) {
		textField.setEditable(editable);
	}
	public String getValue() {
		return textField.getText();
	}
}

@SuppressWarnings("serial")
class LabelledPasswordFieldPanel extends LabelledFieldPanel{
	public LabelledPasswordFieldPanel(String label,int fieldSize) {
		super(label, new JPasswordField(fieldSize));
	}
	public LabelledPasswordFieldPanel(String label,int fieldSize,String defaultString) {
		super(label, new JPasswordField(defaultString,fieldSize));
	}
	public String getValue() {
		return String.copyValueOf(((JPasswordField)textField).getPassword());
	}
}


@SuppressWarnings("serial")
class LabelledComboBoxPanel extends JPanel{
	private JLabel label;
	private JComboBox<String> comboBox ;
	private LinkedList<String> comboBoxValues;
	public LabelledComboBoxPanel (String label,LinkedList<String> comboBoxValues) {
		this.label = new JLabel(label);
		this.comboBoxValues = comboBoxValues;
		comboBox = new JComboBox<String>();
		for (String value : comboBoxValues)
			comboBox.addItem(value);
		add(this.label);
		add(this.comboBox);
		
	}
	public LabelledComboBoxPanel (String label,LinkedList<String> comboBoxValues,String defaultSelection) {
		this.label = new JLabel(label);
		comboBox = new JComboBox<String>();
		for (String value : comboBoxValues)
			comboBox.addItem(value);
		add(this.label);
		add(this.comboBox);
		setSelectedItem(defaultSelection);
		
	}
	public Object getSelectedItem() {
		return comboBox.getSelectedItem();			
	}
	public void setSelectedItem(String object) {
		comboBox.setSelectedItem(object);
	}
	public void setEditable(boolean editable) {
		this.comboBox.setEditable(editable);
		if(editable) {
			 
			String selectedItem = (String)getSelectedItem();
			this.comboBox =new JComboBox<String>();
			for (String value : comboBoxValues)
				comboBox.addItem(value);
			add(this.label);
			add(this.comboBox);
			setSelectedItem(selectedItem);
		}
		else {
			String selectedItem = (String)getSelectedItem();
			this.comboBox =new JComboBox<String>();
			comboBox.addItem(selectedItem);
			comboBox.setSelectedItem(selectedItem);
		}
	}
	
}

@SuppressWarnings("serial")
class NullJFrame extends JFrame{
	public NullJFrame () {
		super();
	}
	public void validate() {
		return;
	}
	public void reValidate() {
		return;
	}
	public void pack() {
		return;
	}
}

@SuppressWarnings("serial")
class UnEditableTableModel extends DefaultTableModel {
	public UnEditableTableModel (Vector<Vector<String>> rowDataVector, Vector<String> columnNamesVector) {
		super(rowDataVector, columnNamesVector);
	}
	public UnEditableTableModel (Object[][] rowDataVector, Object[] columnNamesVector) {
		super(rowDataVector, columnNamesVector);
	}
    public boolean isCellEditable(int row, int column){  
        return false;  
    }
}

interface RefreshableComponent {
	public void refresh();
}

class NullRefreshableComponent implements RefreshableComponent{
	public void refresh() {
		return;
	}
}


@SuppressWarnings("serial")
class CalendarPanel extends JPanel {
 
  private DefaultTableModel model;
  private Calendar cal = new GregorianCalendar();
  private JLabel label;
  private JTable table;
  private int offset;
 
  public CalendarPanel() {
 
//	this.setSize(300,200);
	this.setLayout(new BorderLayout());
    this.setVisible(true);

   
    label = new JLabel();
    label.setHorizontalAlignment(SwingConstants.CENTER);
 
    JButton b1 = new JButton("<-");
    b1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        cal.add(Calendar.MONTH, -1);
        updateMonth();
      }
    });
 
    JButton b2 = new JButton("->");
    b2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        cal.add(Calendar.MONTH, +1);
        updateMonth();
      }
    });
 
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(b1,BorderLayout.WEST);
    panel.add(label,BorderLayout.CENTER);
    panel.add(b2,BorderLayout.EAST);
 
 
    String [] columns = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
//    model = new DefaultTableModel(null,columns);
    model = new UnEditableTableModel(null,columns);
    
    table = new JTable(model);
    table.setCellSelectionEnabled(true);
    JScrollPane pane = new JScrollPane(table);
 
    this.add(panel,BorderLayout.NORTH);
    this.add(pane,BorderLayout.CENTER);
 
    this.updateMonth();
 
  }
 
  public SimpleDate getValue() throws InvalidDateException{
	  int dateRowIndex = table.getSelectedRow();
	  int dateColumnIndex = table.getSelectedColumn();
	  
	  int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	  int date = dateRowIndex*7 + dateColumnIndex + 1 - offset;

	  
	  Date fullDate = cal.getTime();
	  int month = Integer.parseInt((new SimpleDateFormat("MM")).format(fullDate));
	  int year = Integer.parseInt((new SimpleDateFormat("yyyy")).format(fullDate));
	  
	  this.updateMonth();
	  if (date < 1 || date > numberOfDays) {
		  throw new InvalidDateException();
	  }
	  return new SimpleDate(date,month,year);
  }
  
  void updateMonth() {
    cal.set(Calendar.DAY_OF_MONTH, 1);
 
    String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
    int year = cal.get(Calendar.YEAR);
    label.setText(month + " " + year);
 
    int startDay = cal.get(Calendar.DAY_OF_WEEK);
    int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    int weeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
 
    model.setRowCount(0);
    model.setRowCount(weeks);
 
    offset = startDay-1;
    int i = offset;
    for(int day=1;day<=numberOfDays;day++){
      model.setValueAt(day, i/7 , i%7 );    
      i = i + 1;
    }
  }
}

@SuppressWarnings("serial")
class InvalidDateException extends Exception{
	public InvalidDateException(){
		super("Invalid Date selected");
	}
}


@SuppressWarnings("serial")
class TimePanel extends JPanel{
	private JComboBox<String> hourComboBox ;
	private JComboBox<String> minuteComboBox;
	private int[] hourValues = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
	private int[] minuteValues = {0,1,2,3,4,5,6,7,8,9,
								10,11,12,13,14,15,16,17,18,19,
								20,21,22,23,24,25,26,27,28,29,
								30,31,32,33,34,35,36,37,38,39,
								40,41,42,43,44,45,46,47,48,49,
								50,51,52,53,54,55,56,57,58,59,60};
								
	public TimePanel () {
		JLabel hourLabel = new JLabel("Hours:");
		hourComboBox = new JComboBox<String>();
		for (int value : hourValues)
			hourComboBox.addItem(Integer.toString(value));
		JLabel minuteLabel = new JLabel("Minutes:");
		minuteComboBox = new JComboBox<String>();
		for (int value : minuteValues)
			minuteComboBox.addItem(Integer.toString(value));
		
		add(hourLabel);
		add(this.hourComboBox);
		add(minuteLabel);
		add(this.minuteComboBox);
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
	}
	public SimpleTime getSelectedTime() {
		int hours = Integer.parseInt((String)hourComboBox.getSelectedItem());
		int minutes = Integer.parseInt((String)minuteComboBox.getSelectedItem());
		return new SimpleTime(hours,minutes);			
	}
	public void setSelectedTime(SimpleTime time) {
		hourComboBox.setSelectedItem(Integer.toString(time.getHours()));
		minuteComboBox.setSelectedItem(Integer.toString(time.getMinutes()));
	}
	public void setEditable(boolean editable) {
		this.hourComboBox.setEditable(editable);
		this.minuteComboBox.setEditable(editable);
	}
}
	
