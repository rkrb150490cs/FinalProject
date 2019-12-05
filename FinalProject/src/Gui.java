
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Gui {
	private JFrame mainWindow;
	private JPanel seatPanel;
	private JButton selected =new JButton("-1");
	
	private Color selectedColor = Color.YELLOW;
	private Color availableColor = Color.GREEN;
	private Color unAvailableColor = Color.LIGHT_GRAY;

	private JButton createAvailableSeat(int x, int y, int seatNumber) {
        JButton b1=new JButton(String.format("%d", seatNumber));  
        b1.addActionListener(new ChangeColorActionListener());
        b1.setLocation(x, y);
        b1.setSize(50, 50);
        b1.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        b1.setBackground(availableColor); 
        return b1;
	}
	
	private JButton createNonAvailableSeat(int x, int y, int seatNumber) {
        JButton b1=new JButton(String.format("%d", seatNumber));
        b1.addActionListener(new ChangeColorActionListener());
        b1.setLocation(x, y);
        b1.setSize(50, 50);
        b1.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        b1.setBackground(unAvailableColor);
        b1.setEnabled(false);
        return b1;
	}
	
	private class ChangeColorActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			String button = event.getActionCommand();
			Component clickedComponent =  seatPanel.getComponent(Integer.parseInt(button));
			JButton clickedButton = (JButton)clickedComponent;
			if (clickedButton.getBackground() == availableColor) {
				selected.setBackground(availableColor);
				selected = clickedButton;
				clickedButton.setBackground(selectedColor);
			}
			else {
				clickedButton.setBackground(availableColor);
				selected =new JButton("-1");
			}
		}
	}

	private class SelectSeatActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			System.out.println(selected.getText());
		}
		
	}
	public Gui() {
        
        int buttonSize = 50;
		int buttonPadding = 5;
		int aisleWidth = 20;
		
        int numberOfSeatsinColumn = 4;
        int numberOfSeatsinRow = 10;
        
        int busFrontheader_width = 200;
        int busFrontheader_height = 30;
        int seatPanelWidth = numberOfSeatsinColumn*(buttonSize + buttonPadding)+ buttonPadding + aisleWidth;
        int busFrontheader_padding = (seatPanelWidth - busFrontheader_width)/2;
        int seatPanelHeight = numberOfSeatsinRow*(buttonSize + buttonPadding) + busFrontheader_padding + busFrontheader_height;
        
        int seatPanelPadding = 40;
//        int seatPanelPadding = 0;
        
		JButton busFrontheader = new JButton ("Front of Bus");

        busFrontheader.setLocation(busFrontheader_padding, busFrontheader_padding);
        busFrontheader.setSize(busFrontheader_width, busFrontheader_height);
        busFrontheader.setEnabled(false);
        busFrontheader.setBackground(Color.WHITE);
        
        seatPanel=new JPanel();
        seatPanel.add(busFrontheader);
        seatPanel.setBounds(seatPanelPadding,seatPanelPadding,seatPanelWidth,seatPanelHeight);    
        seatPanel.setBackground(Color.GRAY);
        seatPanel.setLayout(null);
        seatPanel.setLocation(seatPanelPadding, seatPanelPadding);
        seatPanel.setBorder(BorderFactory.createEmptyBorder());
        
        int y = busFrontheader_padding + busFrontheader_height + buttonPadding;
        int x = buttonPadding;int count = 1;
        for (int j = 1;j<=numberOfSeatsinRow; j++) {
        	x = buttonPadding;
	        for (int i = 1;i<=numberOfSeatsinColumn;i++) {
	        	JButton b;
	        	if (! (count%9==0))
	        		b = createAvailableSeat(x,y,count);
	        	else
	        		b = createNonAvailableSeat(x,y,count);
	        	seatPanel.add(b);
	        	count++;
	        	if (i==2)
	        		x+=aisleWidth;
	        	x+=(buttonSize + buttonPadding);
	        }
	        y+=(buttonSize + buttonPadding);
        }
        

        int OkbuttonPading = 10;
        int OkbuttonWidth = 100;
        int OkbuttonHeight = 30;
        JButton selectSeatButton = new JButton("Select Seat");
        selectSeatButton.addActionListener(new SelectSeatActionListener());
        int locationX = (seatPanelPadding + seatPanelWidth - OkbuttonWidth)/2;
        int locationY = 2*seatPanelPadding + seatPanelHeight + OkbuttonPading;
        selectSeatButton.setLocation(locationX, locationY);
        selectSeatButton.setSize(OkbuttonWidth, OkbuttonHeight); 
        
        
        int windowWidth = 2*seatPanelPadding + seatPanelWidth ;
        int windowHeight = locationY + OkbuttonPading + OkbuttonHeight;
        
        JPanel mainPanel= new JPanel();    
        mainPanel.setLayout(null);
        mainPanel.add(seatPanel);
        mainPanel.add(selectSeatButton);
        mainPanel.setPreferredSize(new Dimension(windowWidth,windowHeight));
        
        mainWindow = new JFrame("Select Seats");
        mainWindow.getContentPane().add(mainPanel);
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainWindow.setVisible(true);
	}
}
