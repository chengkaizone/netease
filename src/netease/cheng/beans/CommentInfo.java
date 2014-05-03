package netease.cheng.beans;

import java.io.Serializable;

public class CommentInfo implements Serializable{
	private String content;
	private String source;
	private String username;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
