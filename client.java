import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
public class client
{
    JFrame frame;
    JButton login,logout,exit,send,b5,clear;
    JTextArea incoming;
    JList online;
    JTextField outgoing;
    JLabel l1,l2;
    Font f,f1;
    JPanel pan,pan1,pan2,pan3,pan4,pan5;
    String user;
    Socket sock;
    BufferedReader br;
    PrintWriter pw;
    ImageIcon img,sen,ex,log,onl,title_icon;
    Image im;
    JScrollPane j1,j2;
    static final int PORT=1025;
    ArrayList<String> names=new ArrayList<String>();


    public void initialize()

    {

	pan=new JPanel();
	pan1=new JPanel();
	pan2=new JPanel();
	pan3=new JPanel();
	pan4=new JPanel();
	pan5=new JPanel();
	f=new Font("Comic Sans MS",Font.BOLD,15);
	f1=new Font("Comic Sans MS",Font.BOLD,35);
	//	img=new ImageIcon("labels/fb.png");
	sen=new ImageIcon("labels/send.png");
	ex=new ImageIcon("labels/exit.png");
	log=new ImageIcon("labels/login.gif");
	onl=new ImageIcon("labels/users.gif");
	l1=new JLabel("CHAT SERVER");
	l1.setFont(f1);
	l2=new JLabel(onl);
	login=new JButton("Login",log);
	online=new JList();
	online.setVisibleRowCount(10);
	online.setFixedCellWidth(250);
	online.setFixedCellHeight(20);
	online.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	online.setFont(f);
	//	incoming.setFont(f1);
	logout=new JButton("Logout");
	exit=new JButton("Exit",ex);
	send=new JButton("Send",sen);
	send.setToolTipText("Send the message");
	b5=new JButton("Clear Chat");
	b5.setToolTipText("Clear Chat Window.");
	clear=new JButton("Clear");
	clear.setToolTipText("Clear the input area.");
	incoming=new JTextArea(17,35);
	incoming.setToolTipText("Chat Window");
	outgoing=new JTextField(20);
	outgoing.setToolTipText("Type your Message Here.");
	incoming.setBackground(Color.black);
	incoming.setForeground(Color.white);
	online.setBackground(Color.black);
	online.setForeground(Color.white);
	online.setToolTipText("Show Online Clients");
	outgoing.setFocusable(false);
	logout.setEnabled(false);
	login.setEnabled(false);
	login.setToolTipText("Click to Login and Chat.");
	title_icon=new ImageIcon("labels/chat.gif");
	im=title_icon.getImage();
	frame.setIconImage(im);
	j1=new JScrollPane(incoming);
	j2=new JScrollPane(online);
	j1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	j1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
	j2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	j2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	incoming.setLineWrap(true);
	incoming.setEditable(false);
	incoming.addKeyListener(new buttons());
	outgoing.addKeyListener(new buttons());
	login.addActionListener(new buttons());
	logout.addActionListener(new buttons());

    }


    public void go()
    
    {
	frame=new JFrame("Client Window");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocation(700,350);
	
	try
	    {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		SwingUtilities.updateComponentTreeUI(frame.getContentPane());
	    }
	catch(Exception ex){}


	initialize();
	logout.setToolTipText("Logout Chat");
	exit.addActionListener(new buttons());
	exit.setToolTipText("Logout and Exit");
	send.addActionListener(new buttons());
	b5.addActionListener(new buttons());
	clear.addActionListener(new buttons());
	pan5.add(l1);
	pan5.setBackground(Color.white);
	pan1.add(j1);
	pan2.add(login);
	pan2.add(clear);
	pan2.add(outgoing);
	pan2.add(send);
	pan2.add(b5);
	pan2.add(logout);
	pan2.add(exit);
	pan4.add(BorderLayout.NORTH,l2);
	pan4.add(j2);
	pan.add(pan2);
	pan.add(pan3);
	pan.add(pan4);
	pan1.setBackground(Color.white);
	pan2.setBackground(Color.white);
	pan4.setBackground(Color.white);
	pan.setBackground(Color.white);
	frame.getContentPane().add(pan4);
	frame.getContentPane().add(BorderLayout.NORTH,pan5);
	frame.getContentPane().add(BorderLayout.WEST,pan1);
	frame.getContentPane().add(BorderLayout.SOUTH,pan);
	j1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
	    {  
		public void adjustmentValueChanged(AdjustmentEvent e) 
		{  
		    e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
		}
	    });


	class Message implements Runnable
	{

	    String msg;
	    public void run()

	    {
		String n;
		try
		    {
			
			while((msg=br.readLine())!=null)
			    {

				if(msg.startsWith("$"))
				    {

					if((names.contains(n=msg.substring(1,msg.length())))==false)
					    {

						incoming.append("****"+n+" Logged in****\n");
						names.add(n);
						online.setListData(names.toArray());

					    }

					else
					    continue;

				    }

				else if(msg.equals("~##~"))
				    {

					if(logout.isEnabled())
					    {
						pw.println("$"+user);
						pw.flush();
					    }

				    }

				else if(msg.startsWith("#"))
					{
					    n=msg.substring(1,msg.length());
					    incoming.append("****"+n+" Logged out****\n");
					    if(names.contains(n));
						{
						    names.remove(n);
						    online.setListData(names.toArray());
						}
					}

				else
				    {
					incoming.append(msg+"\n");
				    }
			    }
		    }

		catch(SocketException ex)
		    {

			incoming.append(ex+"\n");

			if(logout.isEnabled())
			    {
				int reply=JOptionPane.showConfirmDialog(frame,ex+" Do you want to start server again and continue?","Error",JOptionPane.YES_NO_OPTION);
				if(reply==JOptionPane.YES_OPTION)
				    {
					networking();
					login.setEnabled(false);
					Thread t1=new Thread(new Message());
					t1.start();
				    }
				else
				    System.exit(0);
			    }

			else
			    System.exit(0);
		    }

		catch(Exception x)
		    {
			incoming.append(x+" Exception in run\n");
		    }
	    }
	}

	frame.setSize(750,500);
	frame.setResizable(false);
	frame.setVisible(true);
	frame.addWindowListener(new WindowAdapter()
	    {
		public void windowClosing(WindowEvent we)
		{
		    exit.doClick();
		}
	    });

	networking();
	Thread t1=new Thread(new Message());
	t1.start();

    }


    private void networking()
    {
	try
	    {
		Object[] option={"Manual","Automatic"};
		int reply=JOptionPane.showOptionDialog(frame,"How you want to provide server Address?","Server Address",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[1]);
		if(reply==JOptionPane.YES_OPTION)
		    {
			String server_address=JOptionPane.showInputDialog(frame,"Enter Server Address or PC-Name.","Input Address",JOptionPane.OK_CANCEL_OPTION);
			if(server_address!=null)
			    {
				sock=new Socket(InetAddress.getByName(server_address),PORT);
			    }
			else
			    {
				SocketException sc=new SocketException();
				throw sc;
			    }
		    }


		else
		    {
			InetAddress a=InetAddress.getLocalHost();
			String ipaddress=a.getHostAddress();
			String subnet=ipaddress.substring(0,(ipaddress.lastIndexOf('.'))+1);
			incoming.append("Scaning for Server please wait......\n");
			for(int i=0;i<256;i++)
			    {
				String s1=subnet+""+i;
				incoming.append("pinging "+s1+"\n");
				InetAddress ad=InetAddress.getByName(s1);
				if(ad.isReachable(PORT))
				    {
					sock=new Socket(ad,PORT);
					incoming.append("Server Found\n");
					break;
				    }
			    }
		    }

		InputStreamReader ir=new InputStreamReader(sock.getInputStream());
		br=new BufferedReader(ir);
		pw=new PrintWriter(sock.getOutputStream());
		login.setEnabled(true);
		incoming.append("Connected to Server.please login.\n");
		pw.println("~##~");
		pw.flush();
		login.requestFocus();
	    }


	catch(Exception ex)
	    {
		JOptionPane.showMessageDialog(frame,"Server Not Running or Failed!!","Error",JOptionPane.INFORMATION_MESSAGE);
		incoming.append("Server not running or Connection Problem : "+ex+"\n");
		System.exit(0);
	    }
    }


    class buttons implements ActionListener,KeyListener
    {
	String str;
	public void actionPerformed(ActionEvent ae)
	{
	    if(ae.getSource()==send)
		{
		    if(logout.isEnabled())
			{
			    String str1=outgoing.getText();
			    str=str1.trim();
			    if(str.length()!=0)
				{
				    pw.println(user+" : "+str);
				    pw.flush();
				    outgoing.setText("");
				    outgoing.requestFocus();
				}
			}
		    else
			{
			    JOptionPane.showMessageDialog(frame,"You must Login First","Login",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	    if(ae.getSource()==login)
		{
		    String usr=JOptionPane.showInputDialog(frame,"Enter Your Name.");
		    String user1=usr.trim();
		    user=user1.toUpperCase();
		    if(names.contains(user)==true)
			{
			    JOptionPane.showMessageDialog(frame,"User name Already exists.\nEnter a unique username.","Error",JOptionPane.INFORMATION_MESSAGE); 
			}
		    else if(user.length()!=0)
			{
			    JOptionPane.showMessageDialog(frame,"Login Successful!!");
			    incoming.setText("");
			    pw.println("$"+user);
			    pw.flush();
			    pw.println("@**"+new Date()+"**\n\n\n");
			    pw.flush();
			    outgoing.setFocusable(true);
			    login.setEnabled(false);
			    logout.setEnabled(true);
			    frame.setTitle(user+"'s Chat Window");
			}
		    else
			{
			    JOptionPane.showMessageDialog(frame,"Login Failed!!");
			}
		}

	    if(ae.getSource()==logout)
		{
		    JOptionPane.showMessageDialog(frame,"Logout Successful.");
		    frame.setTitle("Client Window");
		    pw.println("#"+user);
		    pw.flush();
		    incoming.setText("");
		    outgoing.setText("");
		    login.setEnabled(true);
		    login.requestFocus();
		    logout.setEnabled(false);
		    outgoing.setFocusable(false);
		}

	
	    if(ae.getSource()==exit)
		{
		    if(logout.isEnabled())
			{
			    int reply=JOptionPane.showConfirmDialog(frame,"Are You sure?","Exit",JOptionPane.YES_NO_OPTION);
			    if(reply==JOptionPane.YES_OPTION)
				{
				    JOptionPane.showMessageDialog(frame,"Logout Successful","Exit",JOptionPane.INFORMATION_MESSAGE);
				    pw.println("#"+user);
				    pw.flush();
				    System.exit(0);
				}
			}
		    else
			System.exit(0);
		}
	    if(ae.getSource()==clear)
		{
		    outgoing.setText("");
		    outgoing.requestFocus();
		}
	    if(ae.getSource()==b5)
		{
		    incoming.setText("");
		    outgoing.requestFocus();
		}
	}


	public void keyPressed(KeyEvent ke)
	{
	    String s1=outgoing.getText();
	    String s=s1.trim();
	    int key=ke.getKeyCode();
	    if((key==KeyEvent.VK_ENTER)&&(s.length()!=0))
		{
		    send.doClick();
		}
	}

	public void keyReleased(KeyEvent ke)
	{}

	public void keyTyped(KeyEvent ke)
	{}

    }


    public static void main(String args[])
	{
	    new client().go();
	}
}
