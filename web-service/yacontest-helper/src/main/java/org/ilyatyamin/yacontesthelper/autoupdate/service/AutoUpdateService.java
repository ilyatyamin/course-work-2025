package org.ilyatyamin.yacontesthelper.autoupdate.service;

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse;

public interface AutoUpdateService {
    AutoUpdateResponse setOnAutoUpdate(final AutoUpdateRequest autoUpdateRequest);
    void removeFromAutoUpdate(final AutoUpdateDeleteRequest request);
}
