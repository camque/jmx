package com.zerometal.jmx.dto;

import java.io.Serializable;

public class PerformanceEventDTO implements Serializable {

	private static final long serialVersionUID = -6069834550740786415L;

	@SuppressWarnings("rawtypes")
	private Class clazz;
	private boolean sucess;
	private long transactionInMillis;
	private String component;

	public PerformanceEventDTO() {}

	@SuppressWarnings("rawtypes")
	public PerformanceEventDTO(final boolean sucess, final long transactionInMillis, Class clazz, final String component) {
		super();
		this.sucess = sucess;
		this.transactionInMillis = transactionInMillis;
		this.component = component;
		this.clazz = clazz;
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
	public String getComponent() {
		return this.component;
	}
	public void setComponent(final String component) {
		this.component = component;
	}
	@SuppressWarnings("rawtypes")
	public Class getClazz() {
		return this.clazz;
	}
	@SuppressWarnings("rawtypes")
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

}
