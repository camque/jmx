package com.zerometal.jmx.dto;

import java.io.Serializable;

public class PerformanceEventDTO implements Serializable {

	private static final long serialVersionUID = -6069834550740786415L;

	private boolean sucess;
	private long transactionInMillis;
	private String root;
	private String component;

	public PerformanceEventDTO() {}

	public PerformanceEventDTO(final boolean sucess, final long transactionInMillis, final String root, final String component) {
		super();
		this.sucess = sucess;
		this.transactionInMillis = transactionInMillis;
		this.root = root;
		this.component = component;
	}

	public void setResult(final boolean sucess, final long transactionInMillis) {
		this.setSucess(sucess);
		this.setTransactionInMillis(transactionInMillis);
	}
	public long getTransactionInMillis() {
		return this.transactionInMillis;
	}
	public void setTransactionInMillis(final long transactionInMillis) {
		this.transactionInMillis = transactionInMillis;
	}
	public boolean isSucess() {
		return this.sucess;
	}
	public void setSucess(final boolean sucess) {
		this.sucess = sucess;
	}
	public String getRoot() {
		return this.root;
	}
	public void setRoot(final String root) {
		this.root = root;
	}
	public String getComponent() {
		return this.component;
	}
	public void setComponent(final String component) {
		this.component = component;
	}

}
