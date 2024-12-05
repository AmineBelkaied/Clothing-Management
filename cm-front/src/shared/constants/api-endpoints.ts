export const PACKET_ENDPOINTS = {
    BASE: '/packets',
    PAGINATED: '/paginated',
    BY_DATE_RANGE: '/by-date-range',
    BY_DATE: '/by-date',
    VALIDATE: '/validate',
    BATCH_DELETE: '/batch-delete',
    STATUS: '/status',
    ADD_PRODUCTS: '/add-products',
    NOTIFICATIONS_SYNC: '/notifications/sync',
    DUPLICATE: '/duplicate',
    STATUS_SYNC: '/status/sync',
    RUPTURE_SYNC: '/rupture/sync',
    BARCODE_STATUS: '/barcode/status',
    RELATED_PRODUCTS: '/related-products',
    CHECK_VALIDITY: '/check-validity',
    TIMELINE: '/timeline',
    ATTEMPT: '/attempt',
    VALIDATION: '/validation-packets'
};

export const OFFER_ENDPOINTS = {
    BASE: '/offers',
    MODEL_QUANTITIES: '/model-quantities',
    BY_FB_PAGE: '/fb-page',
    BATCH_DELETE: '/batch-delete',
    UPDATE_DATA: '/update-data',
    UPDATE_OFFER_FB_PAGES: '/update-offer-fb-pages',
    UPDATE_OFFER_MODESLS: '/update-offer-models',
    CHECK_OFFER_USAGE: '/check-offer-usage',
    ROLLBACK: '/rollback'
};

export const MODEL_ENDPOINTS = {
    BASE: '/models',
    BATCH_DELETE: '/batch-delete',
    CHECK_MODEL_USAGE: '/check-model-usage',
    ROLLBACK: '/rollback',
};

export const PRODUCT_ENDPOINTS = {
    BASE: '/products',
    BATCH_DELETE: '/batch-delete',
    STOCK: '/stock',
    STOCK_QUANTITY: '/stock-quantity',
    MODEL_IDS: '/byModelIds',

};

export const PRODUCT_HISTORY_ENDPOINTS = {
    BASE: '/product-histories',
    BY_MODEL: '/model',
    BATCH_ADD: '/batch-add',
    BATCH_DELETE: '/batch-delete'
};


export const STAT_ENDPOINTS = {
    BASE: '/stats',
    MODEL: '/model',
    MODELS: '/models',
    MODELS_CHART: '/models-chart',
    STOCK: '/stock',
    COLORS: '/colors',
    PACKETS: '/packets',
    OFFERS: '/offers',
    PRODUCTS: '/products',
    PAGES: '/pages',
    STATES: '/states',
    PACKETSDASHBOARD: '/packets-dachboard'
};

export const CITY_ENDPOINTS = {
    BASE: '/cities',
    GROUPED_BY_GOVERNORATE: '/grouped-by-governorate',
    BATCH_DELETE: '/batch-delete'
};

export const GOVERNORATE_ENDPOINTS = {
    BASE: '/governorates',
    BATCH_DELETE: '/batch-delete'
};

export const FB_PAGE_ENDPOINTS = {
    BASE: '/fb-pages',
    CHECK_FB_PAGE_USAGE: '/check-fb-page-usage'
};

export const DELIVERY_COMPANY_ENDPOINTS = {
    BASE: '/delivery-companies',
    CHECK_DELIVERY_COMPANY_USAGE: '/check-delivery-company-usage'
};

export const COLOR_ENDPOINTS = {
    BASE: '/colors',
    CHECK_COLOR_USAGE: '/check-color-usage'
};

export const SIZE_ENDPOINTS = {
    BASE: '/sizes',
    CHECK_SIZE_USAGE: '/check-size-usage'
};

export const NOTE_ENDPOINTS = {
    BASE: '/notes',
    BY_PACKET: '/packet'
};

export const MODEL_IMAGE_ENDPOINTS = {
    BASE: '/images',
    UPLOAD: '/upload',
    download: '/download'
};

export const USER_ENDPOINTS = {
    BASE: '/users',
    BATCH_DELETE: '/batch-delete'
};

export const ROLE_ENDPOINTS = {
    BASE: '/roles'
};

export const GLOBAL_CONFIG_ENDPOINTS = {
    BASE: '/global-config',
};
