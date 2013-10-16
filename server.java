import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class server
{
	Socket sock;
	JButton exit,clear;
	JFrame frame;
	JTextArea info;
	BufferedReader br;
	PrintWriter pw;
	TrayIcon ic;
	static final int PORT=1025;
	ArrayList connected;




	public static void main(String[] args)
	{
		server ser=new server();
		ser.go();
		ser.networking();
	}



	public void go()
	{
		frame=new JFrame("Server");
		frame.setResizable(false);
		frame.setLocation(750,370);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clear=new JButton("Clear");
		clear.setToolTipText("Clear the information window.");
		ImageIcon ico=new ImageIcon("labels/info.gif");
		ImageIcon icon=new ImageIcon("labels/server.png");
		frame.setIconImage(icon.getImage());
		JLabel l1=new JLabel("Information Messages",ico,JLabel.CENTER);
		l1.setToolTipText("It displays users information those who are connecting or leaving server.");
		Font f=new Font("Comic Sans MS",Font.BOLD,15);
		Font f1=new Font("Ariel",Font.BOLD,15);
		l1.setFont(f1);
		ImageIcon ex=new ImageIcon("labels/exit.png");
		exit=new JButton("Exit",ex);
		info=new JTextArea(13,30);
		info.setFont(f);
		info.setToolTipText("Information Window.");
		exit.setToolTipText("Stop and Exit Server");
		info.setEditable(false);
		info.setLineWrap(true);
		JScrollPane jp=new JScrollPane(info);
		jp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		
		if(SystemTray.isSupported())
		{
			ic=new TrayIcon(icon.getImage());
			ic.setToolTip("Chat Server");
			ic.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent ae)
				{
					frame.setVisible(true);
					frame.setExtendedState(frame.NORMAL);
					SystemTray.getSystemTray().remove(ic);
				}
			});
		}
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(frame);
		}
		catch(Exception e)
		{
			info.append(e+"\n");
		}


		JPanel pane=new JPanel();
		JPanel pane1=new JPanel();
		JPanel pane2=new JPanel();
		jp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{
			public void adjustmentValueChanged(AdjustmentEvent ae)
			{
				ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
			}
		});


		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				JOptionPane.showMessageDialog(frame,"Server Turned off!!");
			}
			public void windowIconified(WindowEvent e)
			{
				frame.setVisible(false);
				try
				{
					SystemTray.getSystemTray().add(ic);
				}
				catch(Exception ex){}
			}
		});


		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource()==clear)
				{
					info.setText("");
				}
			}
		});


		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource()==exit)
				{
					int c=JOptionPane.showConfirmDialog(frame,"Are you Sure?","Conformation",JOptionPane.YES_NO_OPTION);
					if(c==JOptionPane.YES_OPTION)
					{
						System.exit(0);
					}
				}
			}
		});


		pane2.add(BorderLayout.NORTH,l1);
		pane.add(BorderLayout.CENTER,jp);
		frame.add(BorderLayout.NORTH,pane2);
		pane1.add(clear);
		pane1.add(exit);
		frame.getContentPane().add(BorderLayout.CENTER,pane);
		frame.getContentPane().add(BorderLayout.SOUTH,pane1);
		frame.pack();
		frame.setVisible(true);
	}



	class reader implements Runnable
	{
		BufferedReader br;
		public reader(Socket clientsocket)
		{
			try
			{
				sock=clientsocket;
				InputStreamReader ir=new InputStreamReader(sock.getInputStream());
				br=new BufferedReader(ir);
			}
			catch(IOException ex)
			{
				info.append(ex+"\n");
			}
		}



		public void run()
		{
			String msg,n;
			try
			{
				while((msg=br.readLine())!=null)
				{
					telleveryone(msg);
				}
			}
			catch(SocketException ex)
			{
				info.append("Client Disconnected.\n");
			}
			catch(IOException ex)
			{
				info.append(ex+"\n");
			}
		}
	}



	public void networking()
	{
		connected=new ArrayList();
		String con;
		try
		{
			ServerSocket ss=new ServerSocket(PORT);
			info.append("Server Started at port : "+PORT+"\n");
			while(true)
			{
				try
				{
					Socket clientsocket=ss.accept();
					InetAddress a=clientsocket.getInetAddress();
					info.append(a.getHostName()+" at IP Address "+a.getHostAddress()+" Connected to server.\n");
					PrintWriter pw=new PrintWriter(clientsocket.getOutputStream());
					connected.add(pw);
					Thread t1=new Thread(new reader(clientsocket));
					t1.start();
				}
				catch(NullPointerException ex)
				{
					info.append("Null pointer Exception ");
				}
				catch(SocketException e)
				{
					info.append("Connection Reset\n");
				}
			}
		}
		catch(Exception ex)
		{
			info.append("Inside Exception\n");
			info.append("Exception Raised : "+ex+"\n");
		}
	}


	public void telleveryone(String str)
	{
		try
		{
			Iterator ir=connected.iterator();
			while(ir.hasNext())
			{
				PrintWriter pw=(PrintWriter)ir.next();
				pw.println(str);
				pw.flush();
			}
		}
		catch(Exception ex)
		{
			info.append(ex+"\n");
		}
	}
}
