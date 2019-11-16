import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class servidor extends Thread {
    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket server;
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;

    /**
     * Método construtor
     * 
     * @param com do tipo Socket
     */

    public servidor(Socket con) {
        this.con = con;
        try {
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * toda vez que um cliente novo chega ao servidor, esse método é acionado e
     * alocado numa Thread e também fica verificando se existe alguma mensagem nova.
     * 
     * Método run
     */

    public void run() {
        try {

            String msg;
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clientes.add(bfw);
            nome = msg = bfr.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = bfr.readLine();
                sendToAll(bfw, msg);
                System.out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Quando um cliente envia uma mensagem, o servidor recebe e manda esta para
     * todos os outros clientes conectados.
     * 
     * Método usado para enviar mensagem para todos os clients
     */

    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {

        BufferedWriter bwS;

        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

    public void sendToAllServer(BufferedWriter bwSaida, String msg) throws IOException {

        for (BufferedWriter bw : clientes) {
                bw.write(msg + "\n");
                bw.flush();   
        }
    }

    /** método para retornar a data */

    public String getCurrentTime(){
        Calendar calendar = calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.println(formatter.format(calendar.getTime()));
        return ""+formatter.format(calendar.getTime());
    }
    /**
     * Método main
     */

    public static void main(String[] args) {

        try {
            // Cria os objetos necessários para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor");
            JTextField txtPorta = new JTextField("12345");
            Object[] texts = { lblMessage, txtPorta };
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

            while (true) {
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
                Thread t = new servidor(con);
                t.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }// Fim do método main
} // Fim da classe