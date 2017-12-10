package ai.dipl.maja;

import java.awt.EventQueue;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.xml.soap.Text;

import edu.packt.neuralnet.data.DataSet;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Font;
import edu.packt.neuralnet.examples.chapter06.DiagnosisExample;
import edu.packt.neuralnet.learn.Backpropagation;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Toolkit;

public class Main {

	private JFrame frame;
	private JButton btnStart;
	private JLabel lblBrEpoha;
	private JTextField txtBrEpoha;
	private JLabel lblBrNeurSkrivSloj;
	private JTextField txtBrNeurSkrivSloj;
	private JTextField txtStopaUcenja;
	private JLabel lblStopaUcenja;
	private JLabel lblKojiSkupPodataka;
	private JScrollPane scrollPane;
	private JButton btnSacuvajRez;
	private JComboBox<String> ddBolest;
	private JLabel lblBrojEksperimenta;
	private JLabel lblBrojacEksperimenta;
	private Integer brExperimenta;
	private JTextPane txtRezultat;
	private JPanel panelErrorEvolution;
	private JLabel lblNewLabel;
	private JButton btnSauvajGrafik;
	private JButton btnPonitiRezultat;
	
	public Integer getBrExperimenta() {
		return brExperimenta;
	}

	public void setBrExperimenta(Integer brExperimenta) {
		this.brExperimenta = brExperimenta;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("deprecation")
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/ai/dipl/maja/mix02.png")));
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 1608, 829);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[220.00][158.00][219.00,grow][41.00][129.00,grow][][][][][][][][][][][][][][grow]", "[][20.00][25.00][][25.00][][][51.00][grow][][72.00,grow][][]"));
		frame.getContentPane().add(getLblBrojEksperimenta(), "cell 0 0,alignx right");
		frame.getContentPane().add(getLblBrojacEksperimenta(), "cell 1 0");
		frame.getContentPane().add(getLblBrEpoha(), "cell 0 1,alignx trailing,aligny baseline");
		frame.getContentPane().add(getTxtBrEpoha(), "cell 1 1,growx");
		frame.getContentPane().add(getScrollPane(), "flowx,cell 4 1 15 10,grow");
		frame.getContentPane().add(getLblBrNeurSkrivSloj(), "cell 0 2,alignx trailing");
		frame.getContentPane().add(getTxtBrNeurSkrivSloj(), "cell 1 2,growx");
		frame.getContentPane().add(getLblStopaUcenja(), "cell 0 3,alignx trailing");
		frame.getContentPane().add(getTxtStopaUcenja(), "cell 1 3,growx,aligny top");
		frame.getContentPane().add(getLblKojiSkupPodataka(), "cell 0 4,alignx trailing");
		frame.getContentPane().add(getDdBolest(), "cell 1 4,growx");
		frame.getContentPane().add(getBtnStart(), "cell 1 6,alignx right");
		frame.getContentPane().add(getLblNewLabel(), "cell 2 6");
		frame.getContentPane().add(getPanelErrorEvolution(), "cell 0 8 3 3,grow");
		frame.getContentPane().add(getBtnSauvajGrafik(), "cell 1 11");
		
		txtBrEpoha.setNextFocusableComponent(txtBrNeurSkrivSloj);
		txtBrNeurSkrivSloj.setNextFocusableComponent(txtStopaUcenja);
		txtStopaUcenja.setNextFocusableComponent(ddBolest);
		ddBolest.setNextFocusableComponent(btnStart);
		frame.getContentPane().add(getBtnPonitiRezultat(), "cell 16 12");
		frame.getContentPane().add(getBtnSacuvajRez(), "cell 18 12");
		
		ddBolest.addItem("Rak dojke");
		ddBolest.addItem("Dijabetes");
		
		setBrExperimenta(0); 
	}

	public String formValidate() {
		
		if(txtBrEpoha.getText().isEmpty()){
			return "Unesite broj epoha.";
		}
		if(!txtBrEpoha.getText().matches("\\d+$") ){
			return "Broj epoha mora biti cio broj.";
		}
		
		if(txtBrNeurSkrivSloj.getText().isEmpty()){
			return "Unesite broj neurona u skrivenom sloju.";
		}
		if(!txtBrNeurSkrivSloj.getText().matches("\\d+$") ){
			return "Broj neurona u skrivenom sloju mora biti cio broj.";
		}
		
		if(txtStopaUcenja.getText().isEmpty()){
			return "Unesite stopu učenja.";
		}
		if(!txtStopaUcenja.getText().matches("^[0][.][0-9]+") ){
			return "Stopa učenja mora biti realan broj manji od 1.";
		}
		
		return null;
	}
	
	
	public void runCommand() {
		panelErrorEvolution.removeAll();
		
		
		String result = formValidate();
		if (result != null) {
			JOptionPane.showMessageDialog(null, result, "Greška", JOptionPane.ERROR_MESSAGE, null);
			btnStart.setEnabled(true);
        	lblNewLabel.setVisible(false);
			return;
		}
				
		try {
			setBrExperimenta(Integer.valueOf(lblBrojacEksperimenta.getText()) + 1);
			lblBrojacEksperimenta.setText(getBrExperimenta().toString());
			
			String CHOSEN_OPTION = "";
			int numberOfInputs  = 0;
			int numberOfOutputs = 0;
			int[] inputColumns  = null;
			int[] outputColumns = null;
			int typedEpochs = Integer.valueOf(txtBrEpoha.getText());
			int typedNumHdnLayer = Integer.valueOf(txtBrNeurSkrivSloj.getText());
			double typedLearningRate = Double.valueOf(txtStopaUcenja.getText());
			
			DataSet dataSet = new DataSet();
			String option = ddBolest.getSelectedItem().toString();
			
			switch (option) {
				case "Rak dojke":
					CHOSEN_OPTION   = "Breast cancer";
					numberOfInputs  = 9;
					numberOfOutputs = 2;
					
					// load data
					dataSet = new DataSet("data", "breast_cancer_fulldata.txt");
					inputColumns  = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
					outputColumns = new int[] { 9, 10 };
					
					break;
				case "Dijabetes":
					CHOSEN_OPTION   = "Diabetes";
					numberOfInputs  = 8;
					numberOfOutputs = 2;
					
					// load data
					dataSet = new DataSet("data", "diabetes_fulldata.txt");
					inputColumns  = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
					outputColumns = new int[] { 8, 9 };
					
					break;
			}
			
			
			
			DiagnosisExample test = new DiagnosisExample();
			test.setTxtRezultat(txtRezultat);
			test.setPanelErrorEvolution(panelErrorEvolution);

			test.pokreniTest(option, typedEpochs, typedNumHdnLayer, typedLearningRate, numberOfInputs, numberOfOutputs, inputColumns, outputColumns, getBrExperimenta(), dataSet);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		this.btnStart.setEnabled(true);
		this.lblNewLabel.setVisible(false);
	}
	
	public void saveText(JTextPane panel) { 
		File file = null;
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(panelErrorEvolution) == JFileChooser.APPROVE_OPTION) {
		  file = fileChooser.getSelectedFile() ;
		  // save to file
		}
		
	    try {
            if (file == null) {
                return;
            }
            BufferedWriter outFile = new BufferedWriter(new FileWriter(file + ".txt"));
            outFile.write(txtRezultat.getText()); //put in textfile

            outFile.close();
	    }
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
	public void takePicture(JPanel panel) {
		File file = null;
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(panelErrorEvolution) == JFileChooser.APPROVE_OPTION) {
		  file = fileChooser.getSelectedFile();
		  // save to file
		}
		if (file == null) {
            return;
        }
	    BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
	    panel.print(img.getGraphics()); // or: panel.printAll(...);
	    try {
	        ImageIO.write(img, "jpg", file);
	    }
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
	public JButton getBtnStart() {
		if (btnStart == null) {
			btnStart = new JButton("Pokreni");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
						
					 Thread t = new Thread()
				        {
				            public void run() {
				            	btnStart.setEnabled(false);
				            	lblNewLabel.setVisible(true);
				            	runCommand();
				            }
				        };
					t.start();
				}
			});
		}
		return btnStart;
	}
	public JLabel getLblBrEpoha() {
		if (lblBrEpoha == null) {
			lblBrEpoha = new JLabel("Broj epoha:");
		}
		return lblBrEpoha;
	}
	public JTextField getTxtBrEpoha() {
		if (txtBrEpoha == null) {
			txtBrEpoha = new JTextField();
			txtBrEpoha.setColumns(10);
			
		}
		return txtBrEpoha;
	}
	public JLabel getLblBrNeurSkrivSloj() {
		if (lblBrNeurSkrivSloj == null) {
			lblBrNeurSkrivSloj = new JLabel("Broj neurona u skrivenom sloju:");
		}
		return lblBrNeurSkrivSloj;
	}
	public JTextField getTxtBrNeurSkrivSloj() {
		if (txtBrNeurSkrivSloj == null) {
			txtBrNeurSkrivSloj = new JTextField();
			txtBrNeurSkrivSloj.setColumns(10);
		}
		return txtBrNeurSkrivSloj;
	}
	public JTextField getTxtStopaUcenja() {
		if (txtStopaUcenja == null) {
			txtStopaUcenja = new JTextField();
			txtStopaUcenja.setColumns(10);
		}
		return txtStopaUcenja;
	}
	public JLabel getLblStopaUcenja() {
		if (lblStopaUcenja == null) {
			lblStopaUcenja = new JLabel("Stopa u\u010Denja:");
		}
		return lblStopaUcenja;
	}
	public JLabel getLblKojiSkupPodataka() {
		if (lblKojiSkupPodataka == null) {
			lblKojiSkupPodataka = new JLabel("Koji skup podataka \u017Eelite da koristite:");
		}
		return lblKojiSkupPodataka;
	}
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTxtRezultat());
		}	
		return scrollPane;
	}
	public JButton getBtnSacuvajRez() {
		if (btnSacuvajRez == null) {
			btnSacuvajRez = new JButton("Sa\u010Duvaj rezultat");
			btnSacuvajRez.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					saveText(txtRezultat);
				}
			});
		}
		return btnSacuvajRez;
	}
	public JComboBox<String> getDdBolest() {
		if (ddBolest == null) {
			ddBolest = new JComboBox<String>();
		}
		return ddBolest;
	}
	public JLabel getLblBrojEksperimenta() {
		if (lblBrojEksperimenta == null) {
			lblBrojEksperimenta = new JLabel("Broj eksperimenta:");
			lblBrojEksperimenta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblBrojEksperimenta;
	}
	public JLabel getLblBrojacEksperimenta() {
		if (lblBrojacEksperimenta == null) {
			lblBrojacEksperimenta = new JLabel("0");
			lblBrojacEksperimenta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblBrojacEksperimenta;
	}
	public JTextPane getTxtRezultat() {
		if (txtRezultat == null) {
			txtRezultat = new JTextPane();
		}
		return txtRezultat;
	}
	public JPanel getPanelErrorEvolution() {
		if (panelErrorEvolution == null) {
			panelErrorEvolution = new JPanel();
			panelErrorEvolution.setBackground(Color.WHITE);
		}
		return panelErrorEvolution;
	}
	public JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Molim sačekajte");
			lblNewLabel.setIcon(new ImageIcon(Main.class.getResource("/ai/dipl/maja/default.gif")));
			lblNewLabel.setVisible(false);
		}
		return lblNewLabel;
	}
	public JButton getBtnSauvajGrafik() {
		if (btnSauvajGrafik == null) {
			btnSauvajGrafik = new JButton("Sačuvaj grafik");
			btnSauvajGrafik.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					takePicture(panelErrorEvolution);
				}
			});
		}
		return btnSauvajGrafik;
	}
	public JButton getBtnPonitiRezultat() {
		if (btnPonitiRezultat == null) {
			btnPonitiRezultat = new JButton("Poništi");
			btnPonitiRezultat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					txtBrEpoha.setText("");
					txtBrNeurSkrivSloj.setText("");
					txtStopaUcenja.setText("");
					txtRezultat.setText("");
					panelErrorEvolution.removeAll();
					panelErrorEvolution.remove(panelErrorEvolution);
					brExperimenta = 0;
					lblBrojacEksperimenta.setText("0");
				}
			});
		}
		return btnPonitiRezultat;
	}
}
