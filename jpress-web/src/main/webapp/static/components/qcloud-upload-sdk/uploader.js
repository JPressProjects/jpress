"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.UploaderEvent = exports.vodAxios = void 0;
var sha1 = require("js-sha1");
var COS = require("cos-js-sdk-v5");
var eventemitter3_1 = require("eventemitter3");
var axios_1 = require("axios");
var util_1 = require("./util");
var vod_reporter_1 = require("./vod_reporter");
var uuidv4 = require("uuid/v4");
exports.vodAxios = axios_1.default.create();
exports.vodAxios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    if (isNaN(error.code)) {
        error.code = 500;
    }
    return Promise.reject(error);
});
var UploaderEvent;
(function (UploaderEvent) {
    UploaderEvent["video_progress"] = "video_progress";
    UploaderEvent["media_progress"] = "media_progress";
    UploaderEvent["video_upload"] = "video_upload";
    UploaderEvent["media_upload"] = "media_upload";
    UploaderEvent["cover_progress"] = "cover_progress";
    UploaderEvent["cover_upload"] = "cover_upload";
})(UploaderEvent = exports.UploaderEvent || (exports.UploaderEvent = {}));
var Uploader = /** @class */ (function (_super) {
    __extends(Uploader, _super);
    function Uploader(params) {
        var _this = _super.call(this) || this;
        _this.sessionName = "";
        _this.vodSessionKey = "";
        _this.appId = 0;
        _this.reqKey = uuidv4();
        _this.reportId = "";
        // resume from break point
        _this.enableResume = true;
        // apply 请求的超时时间
        _this.applyRequestTimeout = 5000;
        _this.applyRequestRetryCount = 3;
        // commit 请求的超时时间
        _this.commitRequestTimeout = 5000;
        _this.commitRequestRetryCount = 3;
        // 重试请求的等待时间
        _this.retryDelay = 1000;
        _this.validateInitParams(params);
        _this.videoFile = params.mediaFile || params.videoFile;
        _this.getSignature = params.getSignature;
        _this.enableResume = params.enableResume;
        _this.videoName = params.mediaName || params.videoName;
        _this.coverFile = params.coverFile;
        _this.fileId = params.fileId;
        _this.applyRequestTimeout =
            params.applyRequestTimeout || _this.applyRequestTimeout;
        _this.commitRequestTimeout =
            params.commitRequestTimeout || _this.commitRequestTimeout;
        _this.retryDelay = params.retryDelay || _this.retryDelay;
        // custom report metrics
        _this.appId = params.appId || _this.appId;
        _this.reportId = params.reportId || _this.reportId;
        _this.cosAuthTime = 0;
        _this.genFileInfo();
        return _this;
    }
    // set storage
    Uploader.prototype.setStorage = function (name, value) {
        if (!name) {
            return;
        }
        var cname = "webugc_" + sha1(name);
        try {
            localStorage.setItem(cname, value);
        }
        catch (e) { }
    };
    // get storage
    Uploader.prototype.getStorage = function (name) {
        if (!name) {
            return;
        }
        var cname = "webugc_" + sha1(name);
        var result = null;
        try {
            result = localStorage.getItem(cname);
        }
        catch (e) { }
        return result;
    };
    // delete storage
    Uploader.prototype.delStorage = function (name) {
        if (!name) {
            return;
        }
        var cname = "webugc_" + sha1(name);
        try {
            localStorage.removeItem(cname);
        }
        catch (e) { }
    };
    // validate init params
    Uploader.prototype.validateInitParams = function (params) {
        if (!util_1.default.isFunction(params.getSignature)) {
            throw new Error("getSignature must be a function");
        }
        if (params.videoFile && !util_1.default.isFile(params.videoFile)) {
            throw new Error("videoFile must be a File");
        }
    };
    Uploader.prototype.genFileInfo = function () {
        // video file info
        var videoFile = this.videoFile;
        if (videoFile) {
            var lastDotIndex = videoFile.name.lastIndexOf(".");
            var videoName = "";
            // if specified, use it.
            if (this.videoName) {
                if (!util_1.default.isString(this.videoName)) {
                    throw new Error("mediaName must be a string");
                }
                else if (/[:*?<>\"\\/|]/g.test(this.videoName)) {
                    throw new Error('Cant use these chars in filename: \\ / : * ? " < > |');
                }
                else {
                    videoName = this.videoName;
                }
            }
            else {
                // else use the meta info of file
                videoName = videoFile.name.substring(0, lastDotIndex);
            }
            this.videoInfo = {
                name: videoName,
                type: videoFile.name.substring(lastDotIndex + 1).toLowerCase(),
                size: videoFile.size
            };
            this.sessionName += videoFile.name + "_" + videoFile.size + ";";
        }
        // cover file info
        var coverFile = this.coverFile;
        if (coverFile) {
            var coverName = coverFile.name;
            var coverLastDotIndex = coverName.lastIndexOf(".");
            this.coverInfo = {
                name: coverName.substring(0, coverLastDotIndex),
                type: coverName.substring(coverLastDotIndex + 1).toLowerCase(),
                size: coverFile.size
            };
            this.sessionName += coverFile.name + "_" + coverFile.size + ";";
        }
    };
    Uploader.prototype.applyUploadUGC = function (retryCount) {
        if (retryCount === void 0) { retryCount = 0; }
        return __awaiter(this, void 0, void 0, function () {
            function whenError(err) {
                return __awaiter(this, void 0, void 0, function () {
                    return __generator(this, function (_a) {
                        switch (_a.label) {
                            case 0:
                                if (err.code === 500) {
                                    Uploader.host = Uploader.host === util_1.HOST.MAIN ? util_1.HOST.BACKUP : util_1.HOST.MAIN;
                                }
                                self.emit(vod_reporter_1.VodReportEvent.report_apply, {
                                    err: err,
                                    requestStartTime: requestStartTime
                                });
                                self.delStorage(self.sessionName);
                                if (self.applyRequestRetryCount == retryCount) {
                                    if (err) {
                                        throw err;
                                    }
                                    throw new Error("apply upload failed");
                                }
                                return [4 /*yield*/, util_1.default.delay(self.retryDelay)];
                            case 1:
                                _a.sent();
                                return [2 /*return*/, self.applyUploadUGC(retryCount + 1)];
                        }
                    });
                });
            }
            var self, signature, sendParams, videoInfo, coverInfo, vodSessionKey, requestStartTime, response, e_1, applyResult, applyData, vodSessionKey_1, err;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        self = this;
                        return [4 /*yield*/, this.getSignature()];
                    case 1:
                        signature = _a.sent();
                        videoInfo = this.videoInfo;
                        coverInfo = this.coverInfo;
                        vodSessionKey = this.vodSessionKey ||
                            (this.enableResume && this.getStorage(this.sessionName));
                        // resume from break point
                        if (vodSessionKey) {
                            sendParams = {
                                signature: signature,
                                vodSessionKey: vodSessionKey
                            };
                        }
                        else if (videoInfo) {
                            sendParams = {
                                signature: signature,
                                videoName: videoInfo.name,
                                videoType: videoInfo.type,
                                videoSize: videoInfo.size
                            };
                            if (coverInfo) {
                                // upload video together with cover
                                sendParams.coverName = coverInfo.name;
                                sendParams.coverType = coverInfo.type;
                                sendParams.coverSize = coverInfo.size;
                            }
                        }
                        else if (this.fileId && coverInfo) {
                            // alter cover
                            sendParams = {
                                signature: signature,
                                fileId: this.fileId,
                                coverName: coverInfo.name,
                                coverType: coverInfo.type,
                                coverSize: coverInfo.size
                            };
                        }
                        else {
                            throw "Wrong params, please check and try again";
                        }
                        requestStartTime = new Date();
                        _a.label = 2;
                    case 2:
                        _a.trys.push([2, 4, , 5]);
                        return [4 /*yield*/, exports.vodAxios.post("https://" + Uploader.host + "/v3/index.php?Action=ApplyUploadUGC", sendParams, {
                                timeout: this.applyRequestTimeout,
                                withCredentials: false
                            })];
                    case 3:
                        response = _a.sent();
                        return [3 /*break*/, 5];
                    case 4:
                        e_1 = _a.sent();
                        return [2 /*return*/, whenError(e_1)];
                    case 5:
                        applyResult = response.data;
                        // all err code https://user-images.githubusercontent.com/1147375/51222454-bf6ef280-1978-11e9-8e33-1b0fdb2fe200.png
                        if (applyResult.code == 0) {
                            applyData = applyResult.data;
                            vodSessionKey_1 = applyData.vodSessionKey;
                            this.setStorage(this.sessionName, vodSessionKey_1);
                            this.vodSessionKey = vodSessionKey_1;
                            this.appId = applyData.appId;
                            this.emit(vod_reporter_1.VodReportEvent.report_apply, {
                                data: applyData,
                                requestStartTime: requestStartTime
                            });
                            return [2 /*return*/, applyData];
                        }
                        else {
                            err = new Error(applyResult.message);
                            err.code = applyResult.code;
                            return [2 /*return*/, whenError(err)];
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    Uploader.prototype.uploadToCos = function (applyData) {
        return __awaiter(this, void 0, void 0, function () {
            var self, cosParam, cos, uploadCosParams, cosVideoParam, cosCoverParam, requestStartTime, uploadPromises;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        self = this;
                        cosParam = {
                            bucket: applyData.storageBucket + "-" + applyData.storageAppId,
                            region: applyData.storageRegionV5
                        };
                        cos = new COS({
                            getAuthorization: function (options, callback) {
                                return __awaiter(this, void 0, void 0, function () {
                                    var currentTimeStamp, safeExpireTime;
                                    return __generator(this, function (_a) {
                                        switch (_a.label) {
                                            case 0:
                                                currentTimeStamp = util_1.default.getUnix();
                                                safeExpireTime = (applyData.tempCertificate.expiredTime - applyData.timestamp) * 0.9;
                                                if (!(self.cosAuthTime === 0)) return [3 /*break*/, 1];
                                                self.cosAuthTime = currentTimeStamp;
                                                return [3 /*break*/, 3];
                                            case 1:
                                                if (!(self.cosAuthTime &&
                                                    currentTimeStamp - self.cosAuthTime >= safeExpireTime)) return [3 /*break*/, 3];
                                                return [4 /*yield*/, self.applyUploadUGC()];
                                            case 2:
                                                applyData = _a.sent();
                                                self.cosAuthTime = util_1.default.getUnix();
                                                _a.label = 3;
                                            case 3:
                                                callback({
                                                    TmpSecretId: applyData.tempCertificate.secretId,
                                                    TmpSecretKey: applyData.tempCertificate.secretKey,
                                                    XCosSecurityToken: applyData.tempCertificate.token,
                                                    StartTime: applyData.timestamp,
                                                    ExpiredTime: applyData.tempCertificate.expiredTime
                                                });
                                                return [2 /*return*/];
                                        }
                                    });
                                });
                            }
                        });
                        this.cos = cos;
                        uploadCosParams = [];
                        if (this.videoFile) {
                            cosVideoParam = __assign(__assign({}, cosParam), { file: this.videoFile, key: applyData.video.storagePath, onProgress: function (data) {
                                    self.emit(UploaderEvent.video_progress, data);
                                    self.emit(UploaderEvent.media_progress, data);
                                }, onUpload: function (data) {
                                    self.emit(UploaderEvent.video_upload, data);
                                    self.emit(UploaderEvent.media_upload, data);
                                }, onTaskReady: function (taskId) {
                                    self.taskId = taskId;
                                } });
                            uploadCosParams.push(cosVideoParam);
                        }
                        if (this.coverFile) {
                            cosCoverParam = __assign(__assign({}, cosParam), { file: this.coverFile, key: applyData.cover.storagePath, onProgress: function (data) {
                                    self.emit(UploaderEvent.cover_progress, data);
                                }, onUpload: function (data) {
                                    self.emit(UploaderEvent.cover_upload, data);
                                }, onTaskReady: util_1.default.noop });
                            uploadCosParams.push(cosCoverParam);
                        }
                        requestStartTime = new Date();
                        uploadPromises = uploadCosParams.map(function (uploadCosParam) {
                            return new Promise(function (resolve, reject) {
                                cos.sliceUploadFile({
                                    Bucket: uploadCosParam.bucket,
                                    Region: uploadCosParam.region,
                                    Key: uploadCosParam.key,
                                    Body: uploadCosParam.file,
                                    onTaskReady: uploadCosParam.onTaskReady,
                                    onProgress: uploadCosParam.onProgress
                                }, function (err, data) {
                                    // only report video file
                                    if (uploadCosParam.file === self.videoFile) {
                                        self.emit(vod_reporter_1.VodReportEvent.report_cos_upload, {
                                            err: err,
                                            requestStartTime: requestStartTime
                                        });
                                    }
                                    if (!err) {
                                        uploadCosParam.onUpload(data);
                                        return resolve();
                                    }
                                    self.delStorage(self.sessionName);
                                    if (JSON.stringify(err) === '{"error":"error","headers":{}}') {
                                        return reject(new Error("cors error"));
                                    }
                                    reject(err);
                                });
                            });
                        });
                        return [4 /*yield*/, Promise.all(uploadPromises)];
                    case 1: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    Uploader.prototype.commitUploadUGC = function (retryCount) {
        if (retryCount === void 0) { retryCount = 0; }
        return __awaiter(this, void 0, void 0, function () {
            function whenError(err) {
                return __awaiter(this, void 0, void 0, function () {
                    return __generator(this, function (_a) {
                        switch (_a.label) {
                            case 0:
                                if (err.code === 500) {
                                    Uploader.host = Uploader.host === util_1.HOST.MAIN ? util_1.HOST.BACKUP : util_1.HOST.MAIN;
                                }
                                self.emit(vod_reporter_1.VodReportEvent.report_commit, {
                                    err: err,
                                    requestStartTime: requestStartTime
                                });
                                if (self.commitRequestRetryCount == retryCount) {
                                    if (err) {
                                        throw err;
                                    }
                                    throw new Error("commit upload failed");
                                }
                                return [4 /*yield*/, util_1.default.delay(self.retryDelay)];
                            case 1:
                                _a.sent();
                                return [2 /*return*/, self.commitUploadUGC(retryCount + 1)];
                        }
                    });
                });
            }
            var self, signature, vodSessionKey, requestStartTime, response, e_2, commitResult, err;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        self = this;
                        return [4 /*yield*/, this.getSignature()];
                    case 1:
                        signature = _a.sent();
                        this.delStorage(this.sessionName);
                        vodSessionKey = this.vodSessionKey;
                        requestStartTime = new Date();
                        _a.label = 2;
                    case 2:
                        _a.trys.push([2, 4, , 5]);
                        return [4 /*yield*/, exports.vodAxios.post("https://" + Uploader.host + "/v3/index.php?Action=CommitUploadUGC", {
                                signature: signature,
                                vodSessionKey: vodSessionKey
                            }, {
                                timeout: this.commitRequestTimeout,
                                withCredentials: false
                            })];
                    case 3:
                        response = _a.sent();
                        return [3 /*break*/, 5];
                    case 4:
                        e_2 = _a.sent();
                        return [2 /*return*/, whenError(e_2)];
                    case 5:
                        commitResult = response.data;
                        if (commitResult.code == 0) {
                            this.emit(vod_reporter_1.VodReportEvent.report_commit, {
                                data: commitResult.data,
                                requestStartTime: requestStartTime
                            });
                            return [2 /*return*/, commitResult.data];
                        }
                        else {
                            err = new Error(commitResult.message);
                            err.code = commitResult.code;
                            return [2 /*return*/, whenError(err)];
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    Uploader.prototype.start = function () {
        var _this = this;
        var requestStartTime = new Date();
        this.donePromise = this._start()
            .then(function (doneResult) {
            _this.emit(vod_reporter_1.VodReportEvent.report_done, {
                err: { code: 0 },
                requestStartTime: requestStartTime
            });
            return doneResult;
        })
            .catch(function (err) {
            _this.emit(vod_reporter_1.VodReportEvent.report_done, {
                err: {
                    code: (err && err.code) || util_1.default.CLIENT_ERROR_CODE.UPLOAD_FAIL
                },
                requestStartTime: requestStartTime
            });
            throw err;
        });
    };
    Uploader.prototype._start = function () {
        return __awaiter(this, void 0, void 0, function () {
            var applyData;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.applyUploadUGC()];
                    case 1:
                        applyData = _a.sent();
                        return [4 /*yield*/, this.uploadToCos(applyData)];
                    case 2:
                        _a.sent();
                        return [4 /*yield*/, this.commitUploadUGC()];
                    case 3: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    Uploader.prototype.done = function () {
        return this.donePromise;
    };
    Uploader.prototype.cancel = function () {
        this.cos.cancelTask(this.taskId);
    };
    // domain
    Uploader.host = util_1.HOST.MAIN;
    return Uploader;
}(eventemitter3_1.EventEmitter));
exports.default = Uploader;
//# sourceMappingURL=uploader.js.map