package dapur;
import ipsrs.*;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

public class DapurRingkasanPengadaan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    public  DapurCariSuplier suplier=new DapurCariSuplier(null,false);
    public  DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    public  DapurBarang barang=new DapurBarang(null,false);
    private PreparedStatement ps;
    private ResultSet rs;
    private double tagihan=0;
    private int i;
    private String order="order by dapurbarang.nama_brng";

    /** Creates new form DlgProgramStudi
     * @param parent
     * @param modal */
    public DapurRingkasanPengadaan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"Kode Barang","Nama Barang","Satuan","Jenis","Jumlah","Total","Kode Sat"};
        tabMode=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class,java.lang.Object.class
             };
             /*Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };*/
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbDokter.setModel(tabMode);

        tbDokter.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 7; i++) {
            TableColumn column = tbDokter.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(95);
            }else if(i==1){
                column.setPreferredWidth(250);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(140);
            }else if(i==4){
                column.setPreferredWidth(70);
            }else if(i==5){
                column.setPreferredWidth(110);
            }else if(i==6){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbDokter.setDefaultRenderer(Object.class, new WarnaTable());

        NoFaktur.setDocument(new batasInput((byte)15).getKata(NoFaktur));
        kdsup.setDocument(new batasInput((byte)5).getKata(kdsup));
        kdptg.setDocument(new batasInput((byte)25).getKata(kdptg));
        kdbar.setDocument(new batasInput((byte)15).getKata(kdbar));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));          
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }  
        suplier.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(suplier.getTable().getSelectedRow()!= -1){                   
                    kdsup.setText(suplier.getTable().getValueAt(suplier.getTable().getSelectedRow(),0).toString());                    
                    nmsup.setText(suplier.getTable().getValueAt(suplier.getTable().getSelectedRow(),1).toString());
                }  
                kdsup.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        suplier.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgCariPembelianIpsrs")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        suplier.dispose();
                    }                
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });         
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){                   
                    kdptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    nmptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }            
                kdptg.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        barang.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(barang.getTable().getSelectedRow()!= -1){                   
                    kdbar.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(),0).toString());                    
                    nmbar.setText(barang.getTable().getValueAt(barang.getTable().getSelectedRow(),1).toString());
                }   
                kdbar.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        barang.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgCariPembelianIpsrs")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        barang.dispose();
                    }                
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Popup1 = new javax.swing.JPopupMenu();
        MnKodeBarangDesc = new javax.swing.JMenuItem();
        MnKodeBarangAsc = new javax.swing.JMenuItem();
        MnNamaBarangDesc = new javax.swing.JMenuItem();
        MnNamaBarangAsc = new javax.swing.JMenuItem();
        MnKategoriAsc = new javax.swing.JMenuItem();
        MnKategoriDesc = new javax.swing.JMenuItem();
        MnSatuanDesc = new javax.swing.JMenuItem();
        MnSatuanAsc = new javax.swing.JMenuItem();
        MnTotalAsc = new javax.swing.JMenuItem();
        MnTotalDesc = new javax.swing.JMenuItem();
        MnJumlahAsc = new javax.swing.JMenuItem();
        MnJumlahDesc = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        scrollPane1 = new widget.ScrollPane();
        tbDokter = new widget.Table();
        jPanel1 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        label10 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        label9 = new widget.Label();
        LTotal = new widget.Label();
        BtnAll = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelisi4 = new widget.panelisi();
        btnBarang = new widget.Button();
        nmbar = new widget.TextBox();
        kdbar = new widget.TextBox();
        label17 = new widget.Label();
        Jenis = new widget.ComboBox();
        jLabel9 = new widget.Label();
        panelisi3 = new widget.panelisi();
        label15 = new widget.Label();
        NoFaktur = new widget.TextBox();
        label11 = new widget.Label();
        TglBeli1 = new widget.Tanggal();
        label16 = new widget.Label();
        label13 = new widget.Label();
        kdsup = new widget.TextBox();
        kdptg = new widget.TextBox();
        nmsup = new widget.TextBox();
        nmptg = new widget.TextBox();
        btnSuplier = new widget.Button();
        btnPetugas = new widget.Button();
        label12 = new widget.Label();
        TglBeli2 = new widget.Tanggal();

        Popup1.setName("Popup1"); // NOI18N

        MnKodeBarangDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnKodeBarangDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKodeBarangDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnKodeBarangDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKodeBarangDesc.setText("Urutkan Berdasar Kode Barang Descending");
        MnKodeBarangDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnKodeBarangDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnKodeBarangDesc.setName("MnKodeBarangDesc"); // NOI18N
        MnKodeBarangDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnKodeBarangDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKodeBarangDescActionPerformed(evt);
            }
        });
        Popup1.add(MnKodeBarangDesc);

        MnKodeBarangAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnKodeBarangAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKodeBarangAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnKodeBarangAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKodeBarangAsc.setText("Urutkan Berdasar Kode Barang Ascending");
        MnKodeBarangAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnKodeBarangAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnKodeBarangAsc.setName("MnKodeBarangAsc"); // NOI18N
        MnKodeBarangAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnKodeBarangAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKodeBarangAscActionPerformed(evt);
            }
        });
        Popup1.add(MnKodeBarangAsc);

        MnNamaBarangDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnNamaBarangDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnNamaBarangDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnNamaBarangDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnNamaBarangDesc.setText("Urutkan Berdasar Nama Barang Descending");
        MnNamaBarangDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnNamaBarangDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnNamaBarangDesc.setName("MnNamaBarangDesc"); // NOI18N
        MnNamaBarangDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnNamaBarangDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnNamaBarangDescActionPerformed(evt);
            }
        });
        Popup1.add(MnNamaBarangDesc);

        MnNamaBarangAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnNamaBarangAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnNamaBarangAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnNamaBarangAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnNamaBarangAsc.setText("Urutkan Berdasar Nama Barang Ascending");
        MnNamaBarangAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnNamaBarangAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnNamaBarangAsc.setName("MnNamaBarangAsc"); // NOI18N
        MnNamaBarangAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnNamaBarangAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnNamaBarangAscActionPerformed(evt);
            }
        });
        Popup1.add(MnNamaBarangAsc);

        MnKategoriAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnKategoriAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKategoriAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnKategoriAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKategoriAsc.setText("Urutkan Berdasar Jenis Ascending");
        MnKategoriAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnKategoriAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnKategoriAsc.setName("MnKategoriAsc"); // NOI18N
        MnKategoriAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnKategoriAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKategoriAscActionPerformed(evt);
            }
        });
        Popup1.add(MnKategoriAsc);

        MnKategoriDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnKategoriDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKategoriDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnKategoriDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKategoriDesc.setText("Urutkan Berdasar Jenis Descending");
        MnKategoriDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnKategoriDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnKategoriDesc.setName("MnKategoriDesc"); // NOI18N
        MnKategoriDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnKategoriDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKategoriDescActionPerformed(evt);
            }
        });
        Popup1.add(MnKategoriDesc);

        MnSatuanDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnSatuanDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSatuanDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnSatuanDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSatuanDesc.setText("Urutkan Berdasar Satuan Descending");
        MnSatuanDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSatuanDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSatuanDesc.setName("MnSatuanDesc"); // NOI18N
        MnSatuanDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnSatuanDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSatuanDescActionPerformed(evt);
            }
        });
        Popup1.add(MnSatuanDesc);

        MnSatuanAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnSatuanAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSatuanAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnSatuanAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSatuanAsc.setText("Urutkan Berdasar Satuan Ascending");
        MnSatuanAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSatuanAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSatuanAsc.setName("MnSatuanAsc"); // NOI18N
        MnSatuanAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnSatuanAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSatuanAscActionPerformed(evt);
            }
        });
        Popup1.add(MnSatuanAsc);

        MnTotalAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnTotalAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTotalAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnTotalAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTotalAsc.setText("Urutkan Berdasar Total Ascending");
        MnTotalAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTotalAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTotalAsc.setName("MnTotalAsc"); // NOI18N
        MnTotalAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnTotalAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTotalAscActionPerformed(evt);
            }
        });
        Popup1.add(MnTotalAsc);

        MnTotalDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnTotalDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTotalDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnTotalDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTotalDesc.setText("Urutkan Berdasar Total Descending");
        MnTotalDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTotalDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTotalDesc.setName("MnTotalDesc"); // NOI18N
        MnTotalDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnTotalDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTotalDescActionPerformed(evt);
            }
        });
        Popup1.add(MnTotalDesc);

        MnJumlahAsc.setBackground(new java.awt.Color(255, 255, 254));
        MnJumlahAsc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnJumlahAsc.setForeground(new java.awt.Color(50, 50, 50));
        MnJumlahAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnJumlahAsc.setText("Urutkan Berdasar Jumlah Ascending");
        MnJumlahAsc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnJumlahAsc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnJumlahAsc.setName("MnJumlahAsc"); // NOI18N
        MnJumlahAsc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnJumlahAsc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnJumlahAscActionPerformed(evt);
            }
        });
        Popup1.add(MnJumlahAsc);

        MnJumlahDesc.setBackground(new java.awt.Color(255, 255, 254));
        MnJumlahDesc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnJumlahDesc.setForeground(new java.awt.Color(50, 50, 50));
        MnJumlahDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnJumlahDesc.setText("Urutkan Berdasar Jumlah Descending");
        MnJumlahDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnJumlahDesc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnJumlahDesc.setName("MnJumlahDesc"); // NOI18N
        MnJumlahDesc.setPreferredSize(new java.awt.Dimension(280, 26));
        MnJumlahDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnJumlahDescActionPerformed(evt);
            }
        });
        Popup1.add(MnJumlahDesc);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Ringkasan Pengadaan Barang Dapur Kering & Basah ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane1.setComponentPopupMenu(Popup1);
        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbDokter.setComponentPopupMenu(Popup1);
        tbDokter.setName("tbDokter"); // NOI18N
        scrollPane1.setViewportView(tbDokter);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 100));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi1.add(label10);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(170, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('5');
        BtnCari.setToolTipText("Alt+5");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari);

        label9.setText("Total :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(45, 30));
        panelisi1.add(label9);

        LTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTotal.setText("0");
        LTotal.setName("LTotal"); // NOI18N
        LTotal.setPreferredSize(new java.awt.Dimension(120, 30));
        panelisi1.add(LTotal);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelisi1.add(BtnAll);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelisi1.add(BtnPrint);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelisi1.add(BtnKeluar);

        jPanel1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        panelisi4.setName("panelisi4"); // NOI18N
        panelisi4.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi4.setLayout(null);

        btnBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBarang.setMnemonic('4');
        btnBarang.setToolTipText("Alt+4");
        btnBarang.setName("btnBarang"); // NOI18N
        btnBarang.setPreferredSize(new java.awt.Dimension(28, 23));
        btnBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarangActionPerformed(evt);
            }
        });
        panelisi4.add(btnBarang);
        btnBarang.setBounds(734, 10, 28, 23);

        nmbar.setEditable(false);
        nmbar.setName("nmbar"); // NOI18N
        nmbar.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi4.add(nmbar);
        nmbar.setBounds(501, 10, 230, 23);

        kdbar.setName("kdbar"); // NOI18N
        kdbar.setPreferredSize(new java.awt.Dimension(80, 23));
        kdbar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdbarKeyPressed(evt);
            }
        });
        panelisi4.add(kdbar);
        kdbar.setBounds(389, 10, 110, 23);

        label17.setText("Barang :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(65, 23));
        panelisi4.add(label17);
        label17.setBounds(325, 10, 60, 23);

        Jenis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Kering", "Basah" }));
        Jenis.setName("Jenis"); // NOI18N
        panelisi4.add(Jenis);
        Jenis.setBounds(94, 10, 120, 23);

        jLabel9.setText("Jenis Barang :");
        jLabel9.setName("jLabel9"); // NOI18N
        panelisi4.add(jLabel9);
        jLabel9.setBounds(0, 10, 90, 23);

        jPanel1.add(panelisi4, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 73));
        panelisi3.setLayout(null);

        label15.setText("No.Faktur :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label15);
        label15.setBounds(0, 10, 80, 23);

        NoFaktur.setName("NoFaktur"); // NOI18N
        NoFaktur.setPreferredSize(new java.awt.Dimension(207, 23));
        NoFaktur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoFakturKeyPressed(evt);
            }
        });
        panelisi3.add(NoFaktur);
        NoFaktur.setBounds(84, 10, 219, 23);

        label11.setText("Tgl.Beli :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label11);
        label11.setBounds(0, 40, 80, 23);

        TglBeli1.setDisplayFormat("dd-MM-yyyy");
        TglBeli1.setName("TglBeli1"); // NOI18N
        TglBeli1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglBeli1KeyPressed(evt);
            }
        });
        panelisi3.add(TglBeli1);
        TglBeli1.setBounds(84, 40, 95, 23);

        label16.setText("Supplier :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label16);
        label16.setBounds(305, 10, 80, 23);

        label13.setText("Petugas :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label13);
        label13.setBounds(305, 40, 80, 23);

        kdsup.setEditable(false);
        kdsup.setName("kdsup"); // NOI18N
        kdsup.setPreferredSize(new java.awt.Dimension(80, 23));
        panelisi3.add(kdsup);
        kdsup.setBounds(389, 10, 80, 23);

        kdptg.setName("kdptg"); // NOI18N
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptgKeyPressed(evt);
            }
        });
        panelisi3.add(kdptg);
        kdptg.setBounds(389, 40, 80, 23);

        nmsup.setEditable(false);
        nmsup.setName("nmsup"); // NOI18N
        nmsup.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmsup);
        nmsup.setBounds(471, 10, 260, 23);

        nmptg.setEditable(false);
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmptg);
        nmptg.setBounds(471, 40, 260, 23);

        btnSuplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSuplier.setMnemonic('1');
        btnSuplier.setToolTipText("Alt+1");
        btnSuplier.setName("btnSuplier"); // NOI18N
        btnSuplier.setPreferredSize(new java.awt.Dimension(28, 23));
        btnSuplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuplierActionPerformed(evt);
            }
        });
        panelisi3.add(btnSuplier);
        btnSuplier.setBounds(734, 10, 28, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("Alt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        panelisi3.add(btnPetugas);
        btnPetugas.setBounds(734, 40, 28, 23);

        label12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label12.setText("s.d.");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label12);
        label12.setBounds(179, 40, 27, 23);

        TglBeli2.setDisplayFormat("dd-MM-yyyy");
        TglBeli2.setName("TglBeli2"); // NOI18N
        TglBeli2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglBeli2KeyPressed(evt);
            }
        });
        panelisi3.add(TglBeli2);
        TglBeli2.setBounds(208, 40, 95, 23);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
            dispose();  
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){            
            dispose();              
        }else{Valid.pindah(evt,BtnPrint,kdbar);}
}//GEN-LAST:event_BtnKeluarKeyPressed
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void btnSuplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuplierActionPerformed
        suplier.emptTeks();
        suplier.isCek();
        suplier.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        suplier.setLocationRelativeTo(internalFrame1);
        suplier.setAlwaysOnTop(false);
        suplier.setVisible(true);
    }//GEN-LAST:event_btnSuplierActionPerformed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void TglBeli1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglBeli1KeyPressed
        Valid.pindah(evt,NoFaktur,kdsup);
    }//GEN-LAST:event_TglBeli1KeyPressed

    private void NoFakturKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoFakturKeyPressed
        Valid.pindah(evt, BtnKeluar, kdsup);
    }//GEN-LAST:event_NoFakturKeyPressed

    private void kdptgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptgKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmptg.setText(petugas.tampil3(kdptg.getText()));     
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kdsup.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kdbar.requestFocus();       
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_kdptgKeyPressed

    private void TglBeli2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglBeli2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglBeli2KeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbDokter.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        NoFaktur.setText("");
        kdbar.setText("");
        Jenis.setSelectedIndex(0);
        nmbar.setText("");
        kdsup.setText("");
        nmsup.setText("");
        kdptg.setText("");
        nmptg.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            //TCari.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();                 
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());  
            param.put("tanggal1",Valid.SetTgl(TglBeli1.getSelectedItem()+""));  
            param.put("tanggal2",Valid.SetTgl(TglBeli2.getSelectedItem()+""));  
            param.put("parameter","%"+TCari.getText().trim()+"%");   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            Valid.MyReportqry("rptRingkasanPengadaanDapur.jasper","report","::[ Laporan Ringkasan Pengadaan Barang Dapur ]::",
                    "select dapurdetailbeli.kode_brng,dapurbarang.nama_brng,dapurbarang.jenis as namajenis, "+
                    "dapurdetailbeli.kode_sat,kodesatuan.satuan,sum(dapurdetailbeli.jumlah) as jumlah,sum(dapurdetailbeli.total) as total "+
                    "from dapurpembelian inner join dapursuplier on dapurpembelian.kode_suplier=dapursuplier.kode_suplier "+
                    "inner join petugas on dapurpembelian.nip=petugas.nip "+
                    "inner join dapurdetailbeli on dapurpembelian.no_faktur=dapurdetailbeli.no_faktur "+
                    "inner join dapurbarang on dapurdetailbeli.kode_brng=dapurbarang.kode_brng "+
                    "inner join kodesatuan on dapurbarang.kode_sat=kodesatuan.kode_sat "+
                    "where dapurpembelian.tgl_beli between '"+Valid.SetTgl(TglBeli1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(TglBeli2.getSelectedItem()+"")+"' and dapurpembelian.no_faktur like '%"+NoFaktur.getText()+"%' and dapursuplier.nama_suplier like '%"+nmsup.getText()+"%' and petugas.nama like '%"+nmptg.getText()+"%'  and dapurbarang.jenis like '%"+Jenis.getSelectedItem().toString().replaceAll("Semua","")+"%' and dapurbarang.nama_brng like '%"+nmbar.getText()+"%' and "+
                    "(dapurpembelian.no_faktur like '%"+TCari.getText()+"%' or dapurpembelian.kode_suplier like '%"+TCari.getText()+"%' or dapursuplier.nama_suplier like '%"+TCari.getText()+"%' or dapurpembelian.nip like '%"+TCari.getText()+"%' or petugas.nama like '%"+TCari.getText()+"%' or dapurbarang.jenis like '%"+TCari.getText()+"%' or dapurdetailbeli.kode_brng like '%"+TCari.getText()+"%' or dapurbarang.nama_brng like '%"+TCari.getText()+"%' or kodesatuan.satuan like '%"+TCari.getText()+"%') "+
                    " group by dapurdetailbeli.kode_brng "+order,param); 
            
            this.setCursor(Cursor.getDefaultCursor());
        } 
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnAll,BtnAll);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void MnKodeBarangDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKodeBarangDescActionPerformed
        order="order by dapurbarang.kode_brng desc";
        tampil();
    }//GEN-LAST:event_MnKodeBarangDescActionPerformed

    private void MnKodeBarangAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKodeBarangAscActionPerformed
        order="order by dapurbarang.kode_brng asc";
        tampil();
    }//GEN-LAST:event_MnKodeBarangAscActionPerformed

    private void MnNamaBarangDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnNamaBarangDescActionPerformed
        order="order by dapurbarang.nama_brng desc";
        tampil();
    }//GEN-LAST:event_MnNamaBarangDescActionPerformed

    private void MnNamaBarangAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnNamaBarangAscActionPerformed
        order="order by dapurbarang.nama_brng asc";
        tampil();
    }//GEN-LAST:event_MnNamaBarangAscActionPerformed

    private void MnKategoriAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKategoriAscActionPerformed
        order="order by dapurbarang.jenis desc";
        tampil();
    }//GEN-LAST:event_MnKategoriAscActionPerformed

    private void MnKategoriDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKategoriDescActionPerformed
        order="order by dapurbarang.jenis asc";
        tampil();
    }//GEN-LAST:event_MnKategoriDescActionPerformed

    private void MnSatuanDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSatuanDescActionPerformed
        order="order by kodesatuan.satuan desc";
        tampil();
    }//GEN-LAST:event_MnSatuanDescActionPerformed

    private void MnSatuanAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSatuanAscActionPerformed
        order="order by kodesatuan.satuan asc";
        tampil();
    }//GEN-LAST:event_MnSatuanAscActionPerformed

    private void MnTotalAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTotalAscActionPerformed
        order="order by sum(dapurdetailbeli.total) asc";
        tampil();
    }//GEN-LAST:event_MnTotalAscActionPerformed

    private void MnTotalDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTotalDescActionPerformed
        order="order by sum(dapurdetailbeli.total) desc";
        tampil();
    }//GEN-LAST:event_MnTotalDescActionPerformed

    private void MnJumlahAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnJumlahAscActionPerformed
        order="order by sum(dapurdetailbeli.jumlah) asc";
        tampil();
    }//GEN-LAST:event_MnJumlahAscActionPerformed

    private void MnJumlahDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnJumlahDescActionPerformed
        order="order by sum(dapurdetailbeli.jumlah) desc";
        tampil();
    }//GEN-LAST:event_MnJumlahDescActionPerformed

    private void btnBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarangActionPerformed
        barang.emptTeks();
        barang.isCek();
        barang.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        barang.setLocationRelativeTo(internalFrame1);
        barang.setAlwaysOnTop(false);
        barang.setVisible(true);
    }//GEN-LAST:event_btnBarangActionPerformed

    private void kdbarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdbarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Jenis.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            TCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnBarangActionPerformed(null);
        }
    }//GEN-LAST:event_kdbarKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DapurRingkasanPengadaan dialog = new DapurRingkasanPengadaan(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.ComboBox Jenis;
    private widget.Label LTotal;
    private javax.swing.JMenuItem MnJumlahAsc;
    private javax.swing.JMenuItem MnJumlahDesc;
    private javax.swing.JMenuItem MnKategoriAsc;
    private javax.swing.JMenuItem MnKategoriDesc;
    private javax.swing.JMenuItem MnKodeBarangAsc;
    private javax.swing.JMenuItem MnKodeBarangDesc;
    private javax.swing.JMenuItem MnNamaBarangAsc;
    private javax.swing.JMenuItem MnNamaBarangDesc;
    private javax.swing.JMenuItem MnSatuanAsc;
    private javax.swing.JMenuItem MnSatuanDesc;
    private javax.swing.JMenuItem MnTotalAsc;
    private javax.swing.JMenuItem MnTotalDesc;
    private widget.TextBox NoFaktur;
    private javax.swing.JPopupMenu Popup1;
    private widget.TextBox TCari;
    private widget.Tanggal TglBeli1;
    private widget.Tanggal TglBeli2;
    private widget.Button btnBarang;
    private widget.Button btnPetugas;
    private widget.Button btnSuplier;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel1;
    private widget.TextBox kdbar;
    private widget.TextBox kdptg;
    private widget.TextBox kdsup;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.Label label9;
    private widget.TextBox nmbar;
    private widget.TextBox nmptg;
    private widget.TextBox nmsup;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private widget.panelisi panelisi4;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbDokter;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
       Valid.tabelKosong(tabMode);
        try{   
            tagihan=0;         
            ps=koneksi.prepareStatement("select dapurdetailbeli.kode_brng,dapurbarang.nama_brng,dapurbarang.jenis as namajenis, "+
                    "dapurdetailbeli.kode_sat,kodesatuan.satuan,sum(dapurdetailbeli.jumlah) as jumlah,sum(dapurdetailbeli.total) as total "+
                    "from dapurpembelian inner join dapursuplier on dapurpembelian.kode_suplier=dapursuplier.kode_suplier "+
                    "inner join petugas on dapurpembelian.nip=petugas.nip "+
                    "inner join dapurdetailbeli on dapurpembelian.no_faktur=dapurdetailbeli.no_faktur "+
                    "inner join dapurbarang on dapurdetailbeli.kode_brng=dapurbarang.kode_brng "+
                    "inner join kodesatuan on dapurbarang.kode_sat=kodesatuan.kode_sat "+
                    "where dapurpembelian.tgl_beli between ? and ? and dapurpembelian.no_faktur like ? and dapursuplier.nama_suplier like ? and petugas.nama like ?  and dapurbarang.jenis like ? and dapurbarang.nama_brng like ? and "+
                    "(dapurpembelian.no_faktur like ? or dapurpembelian.kode_suplier like ? or dapursuplier.nama_suplier like ? or dapurpembelian.nip like ? or petugas.nama like ? or dapurbarang.jenis like ? or dapurdetailbeli.kode_brng like ? or "+
                    "dapurbarang.nama_brng like ? or kodesatuan.satuan like ?) group by dapurdetailbeli.kode_brng "+order);
            try {
                ps.setString(1,Valid.SetTgl(TglBeli1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(TglBeli2.getSelectedItem()+""));
                ps.setString(3,"%"+NoFaktur.getText()+"%");
                ps.setString(4,"%"+nmsup.getText()+"%");
                ps.setString(5,"%"+nmptg.getText()+"%");
                ps.setString(6,"%"+Jenis.getSelectedItem().toString().replaceAll("Semua","")+"%");
                ps.setString(7,"%"+nmbar.getText()+"%");
                ps.setString(8,"%"+TCari.getText()+"%");
                ps.setString(9,"%"+TCari.getText()+"%");
                ps.setString(10,"%"+TCari.getText()+"%");
                ps.setString(11,"%"+TCari.getText()+"%");
                ps.setString(12,"%"+TCari.getText()+"%");
                ps.setString(13,"%"+TCari.getText()+"%");
                ps.setString(14,"%"+TCari.getText()+"%");
                ps.setString(15,"%"+TCari.getText()+"%");
                ps.setString(16,"%"+TCari.getText()+"%");
                rs=ps.executeQuery();
                while(rs.next()){
                    tagihan=tagihan+rs.getDouble("total");
                    tabMode.addRow(new Object[]{
                        rs.getString("kode_brng"),rs.getString("nama_brng"),rs.getString("satuan"),rs.getString("namajenis"),rs.getDouble("jumlah"),rs.getDouble("total"),rs.getString("kode_sat")
                    });   
                }                
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }   
            LTotal.setText(Valid.SetAngka(tagihan));
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }        
    }

    public void emptTeks() {
        kdbar.setText("");
        nmbar.setText("");
        kdbar.requestFocus();        
    }
    
    public void isCek(){
        BtnPrint.setEnabled(akses.getdapur_ringkasan_pembelian());
    }
    
}
