package zad1;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class JListGUI extends javax.swing.JFrame
	{

		private static final long serialVersionUID = 1L;

		public JListGUI(List<String> offerList) {		
			JPanel panel = new JPanel();
			JList<String> list = new JList<String>();
			
			DefaultListModel<String>	lm = new DefaultListModel<String>();
			for(int i=0;i<offerList.size();i++) {
				lm.addElement(offerList.get(i));
			}
			
			setTitle("Offers");
			panel.setLayout( new BorderLayout() );
			getContentPane().add(panel);
			list.setModel(lm);				

	

			panel.add(new JScrollPane(list));
					
		}
	}

