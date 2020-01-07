package com.hss01248.http.old;

/**
 * Created by hss on 2018/6/9.
 */

/*public class RetrofitConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    RetrofitConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String json = value.string();
            JsonReader jsonReader = gson.newJsonReader(new InputStreamReader(new ByteArrayInputStream(json.getBytes())));
            T data =  adapter.read(jsonReader);
           *//* if(data instanceof BaseNetBean){
                BaseNetBean data = (BaseNetBean) data;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    data.dataStr = jsonObject.optString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                //其他情况
            }*//*
            return data;
        }finally {
            value.close();
        }
    }
}*/
