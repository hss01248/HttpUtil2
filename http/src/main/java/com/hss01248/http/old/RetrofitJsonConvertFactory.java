package com.hss01248.http.old;

/**
 * Created by hss on 2018/6/9.
 */

/*public class RetrofitJsonConvertFactory extends Converter.Factory {

    public static RetrofitJsonConvertFactory create() {
        return create(new Gson());
    }
    public static RetrofitJsonConvertFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new RetrofitJsonConvertFactory(gson);
    }

    private final Gson gson;

    private RetrofitJsonConvertFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Gson gson = new Gson();
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new RetrofitConverter<>(gson, adapter);
    }
}*/
