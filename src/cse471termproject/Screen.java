package cse471termproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Screen implements ActionListener, Runnable {
	
	private static JFrame frame;
	private JLayeredPane pane;
	static JLabel label = new JLabel("Welcome");    
	
	public static boolean flag = true;
	public static boolean startflag = true;
	public static boolean stopflag = true;
	
public static boolean stoppls = true;
	
	public static boolean isStoppls() {
		return stoppls;
	}

	public static void setStoppls(boolean stoppls) {
		Screen.stoppls = stoppls;
	}
	

	public static void GUI() throws Exception
	{
		JFrame frame = new JFrame("Term Project");
		
		label.setForeground(Color.BLACK);
    	label.setFont(new Font("Arial", Font.BOLD, 50));
    	label.setBounds(293, 10, 100, 20);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 450);
        
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem help = new JMenuItem("Help");
        
        mb.add(file);
        
        mb.add(help);
        help.addActionListener(new help());
        
        JMenuItem start = new JMenuItem("Start");
        file.add(start);
        start.addActionListener(new start());

        JMenuItem stop = new JMenuItem("Stop");
        file.add(stop);
        stop.addActionListener(new stop());

        JMenuItem report = new JMenuItem("Report");
        file.add(report);
        report.addActionListener(new report());
        
        JMenuItem ahtf = new JMenuItem("Add host to filter");
        file.add(ahtf);
        ahtf.addActionListener(new ahtf());
        
        JMenuItem dcfh = new JMenuItem("Display current filtered hosts");
        file.add(dcfh);
        dcfh.addActionListener(new dcfh());
        
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);
        exit.addActionListener(new exit());
        
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, label);
        frame.setVisible(true);
        
	}
	@Override
	public void actionPerformed(ActionEvent e) {		
			
	}

	static class start implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	label.setText("Proxy initiated");
        	try {
        		new Thread() {
        			@Override
        			public void run() {
        				try {
        					new Thread(Proxy.ProxyOpen());
        				} catch (Exception e1) {
        					e1.printStackTrace();
        				}
        			}
        		}.start();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	
        	
        }
    }
	
	static class stop implements ActionListener
    {
        @SuppressWarnings({ "removal", "deprecation" })
		public void actionPerformed(ActionEvent e)
        {
        	label.setText("Proxy Closed");
        	
        }
    }
	
	static class report implements ActionListener
	{
      public void actionPerformed(ActionEvent e)
      {
    	  try {
			Proxy.reportfile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	  
    	  File file = new File("report.txt");
    	  String[] lines;
    	  FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	  BufferedReader br = new BufferedReader(fr);
    	  String s;
    	  String input = JOptionPane.showInputDialog(frame, "Enter IP:");
    	  String PrintString = "";
    	  String newline = "\n";
    	  try {
			while((s=br.readLine()) != null)
			  {
				  lines = s.split(" ");
				  for(String x : lines)
				  {
					  if(x.equals(input))
					  {
						  PrintString = newline + s + PrintString;
					  }
				  }
				  
			  }
			JOptionPane.showMessageDialog(frame,PrintString);
		} catch (HeadlessException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    			 
      }
	}
	
	static class ahtf implements ActionListener
	{
      public void actionPerformed(ActionEvent e)
      {
      	String result = JOptionPane.showInputDialog(frame, "Enter URL:");
      	Proxy.addFobdn(result);
      }
	}
	
	static class dcfh implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	String liste = "";
        	int count = Proxy.getForbcounter();
        	for(int i = 0;i < count ; i++)
        	{
        		liste = liste.concat(Proxy.getGetforbd(i)).concat("\n");
        		
        	}
        	JOptionPane.showMessageDialog(frame,liste);
        }
    }
	
	static class help implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JOptionPane.showMessageDialog(frame,"Doðukan Mert Doðru 20170702071");
        }
    }
	
	static class exit implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }

	@Override
	public void run() {
		
	}
	
	
}
