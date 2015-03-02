package beans;

/**
 * Created by dimuthuupeksha on 3/2/15.
 */
public class RequestFileAck implements Message{
    private int fileCount;
    private String ip;
    private int port;
    private int hops;
    private String[] files;

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    @Override
    public String convertToQuery() {
        String query = "SEROK";
        query =query+" "+fileCount;
        if(fileCount>0 && fileCount<9998){
            query += " " + ip + " " + port + " " + hops;
            if(files!=null){
                for(int i=0;i<files.length;i++){
                    query+= " "+files[i];
                }
            }
        }

        int queryLength = query.length()+5;

        query = String.format("%04d", queryLength) + " " + query;
        return query;
    }

    @Override
    public boolean initialize(String query) {
        String parts[] = query.split(" ");
        if(parts.length<3){
            return false;
        }else if(parts.length>=3 && parts.length<7){
            fileCount = Integer.parseInt(parts[2]);
        }else if(parts.length>=7){
            fileCount = Integer.parseInt(parts[2]);
            ip = parts[3];
            port = Integer.parseInt(parts[4]);
            hops = Integer.parseInt(parts[5]);

            files = new String[fileCount];
            for (int i=0; i<fileCount; i++){
                files[i] = parts[i+6];
            }
            return true;
        }

        return false;
    }
}
