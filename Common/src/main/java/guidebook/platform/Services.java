package guidebook.platform;

import technology.roughness.whitenoise.platform.ServicesBase;

import guidebook.api.GuidebookAPI;
import guidebook.platform.services.IBookHelper;

public class Services extends ServicesBase {

    public static final IBookHelper BOOK_HELPER = load(GuidebookAPI.LOGGER, IBookHelper.class);

}
