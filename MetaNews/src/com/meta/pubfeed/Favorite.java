package com.meta.pubfeed;

public class Favorite {

	private int _fav;
	private String _title;
	private String _image;
	private String _desc;
	private String _date;
	

	public void setFav(int authorname) 
	{
		_fav = authorname;
	}

	public int getFav() {
		return _fav;
	}

	public void setFavTitle(String title) {
		_title = title;
	}

	public String getFavTitle() {
		return _title;
	}

	public void setImage(String image) {
		_image = image;
	}

	public String getFavImage() {
		return _image;
	}

	public void setFavDesc(String desc) {
		_desc = desc;
	}

	public String getFavDesc() {
		return _desc;
	}

	public void setDate(String dat) {
		_date = dat;
	}

	public String getDate() {
		return _date;
	}

}
