package htmlGenerator;

public enum HTMLType {

    DIV,LI,UL,SPAN,ROOT,H1;
    
    public String getHtml(String content) {
        return "<" + this.name() + ">"+content+"</" + this.name() + "> ";
    }
}
