package handbook.platform;

import technology.roughness.whitenoise.platform.ServicesBase;

import handbook.api.HandbookAPI;
import handbook.platform.services.IBookHelper;

public class Services extends ServicesBase {

    public static final IBookHelper BOOK_HELPER = load(HandbookAPI.LOGGER, IBookHelper.class);

}
