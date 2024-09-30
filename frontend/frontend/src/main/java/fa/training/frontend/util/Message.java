package fa.training.frontend.util;

import lombok.Getter;

@Getter
public enum Message {
    //1xx User error Example: violate validation, incorrect username/password...
    //2xx Success
    //3xx Server error Example: exception

    //10x Auth and file
    MSG_101(101,"Sorry, your username or password is incorrect. Please try again!"),
    MSG_102(102,"File extension is not supported"),
    MSG_103(103,"Invalid file name"),
    MSG_104(104,"Invalid sheet name"),
    MSG_105(105, "No data"),
    MSG_106(105,"File is empty"),

    //11x Customer
    MSG_111(111, "Customer is not exist"),
    MSG_112(112, "Customer's identity card is duplicated"),
    MSG_113(113, "Customer's phone is duplicated"),
    MSG_114(114, "Customer's username is duplicated"),
    MSG_115(115, "Customer's email is duplicated"),
    MSG_116(116, "Customer's status is invalid"),

    //12x Vaccine type
    MSG_121(121, "The selected vaccine type is inactive"),
    MSG_122(122, "No vaccine type is selected"),
    MSG_123(123, "Vaccine type code is duplicated"),
    MSG_124(124, "Vaccine type name is duplicated"),
    MSG_125(125, "No data to make inactive!"),
    MSG_126(126, "Invalid data - please recheck your selects!"),
    MSG_127(127, "Update vaccine type image fail"),
    MSG_128(128, "Vaccine type is not exist"),

    //13x news type
    MSG_131(131, "News type is not exist"),

    //14x news
    MSG_141(141, "News is not exist"),

    //15x Vaccine
    MSG_151(151, "Vaccine status is invalid"),
    MSG_152(152, "Inactivate vaccines fail"),
    MSG_153(153, "Vaccine is not exist"),

    //16x Employee
    MSG_161(161, "Employee is not exist"),
    MSG_162(162, "Employee's phone is duplicated"),
    MSG_163(163, "Employee's username is duplicated"),
    MSG_164(164, "Employee's email is duplicated"),
    //17x Injection schedule
    MSG_171(171, "Injection schedule is not exist"),

    //18x Injection result
    MSG_181(181, "Insufficient number of injection"),
    MSG_182(182, "Injection result not found"),
    MSG_183(183, "Injection result is not exist"),

    //19x other


    //20x Auth and file
    MSG_201(201,"Login successfully"),
    MSG_202(202, "Get file url successfully"),
    MSG_203(203, "Delete file successfully"),
    MSG_204(204, "File uploaded successfully"),

    //21x Customer
    MSG_211(211, "Get all customers successfully"),
    MSG_212(212, "Create customer successfully"),
    MSG_213(213, "Get customer successfully"),
    MSG_214(214, "Update customer successfully"),
    MSG_215(215, "Switch customer status successfully"),
    MSG_216(216, "Delete customer successfully"),

    //22x Vaccine type
    MSG_221(221, "Add vaccine type successfully"),
    MSG_222(222, "Get vaccine type list successfully"),
    MSG_223(223, "Get vaccine type successfully"),
    MSG_224(224, "Update vaccine type successfully"),
    MSG_225(225, "Vaccine Type(s) have been successfully made inactive."),
    MSG_226(226, "Delete vaccine type successfully"),

    //23x news type

    MSG_231(231, "Get news type list successfully"),
    MSG_232(232, "Get news type successfully"),
    MSG_233(233, "Add new news type successfully"),
    MSG_234(234, "Update news type successfully"),
    MSG_235(235, "Delete news type successfully"),
    //24x news

    MSG_241(241, "Get news list successfully"),
    MSG_242(242, "Get news successfully"),
    MSG_243(243, "Add new news successfully"),
    MSG_244(244, "Update news successfully"),
    MSG_245(245, "Delete news successfully"),

    //25x Vaccine
    MSG_251(251, "Get all vaccines successfully"),
    MSG_252(252, "Create vaccine successfully"),
    MSG_253(253, "Get vaccine successfully"),
    MSG_254(254, "Update vaccine successfully"),
    MSG_255(255, "Switch vaccine status successfully"),
    MSG_256(256, "Get all vaccine names successfully"),
    MSG_257(256, "Inactivate vaccines successfully"),

    //26x Employee
    MSG_261(261, "Get employee list successfully"),
    MSG_262(262, "Get employee successfully"),
    MSG_263(263, "Add new employee successfully"),
    MSG_264(264, "Update employee successfully"),
    MSG_265(265, "Delete employee successfully"),

    //27x Injection schedule
    MSG_271(271, "Get injection schedule list successfully"),
    MSG_272(272, "Get injection schedule successfully"),
    MSG_273(273, "Add new injection schedule successfully"),
    MSG_274(274, "Update injection schedule successfully"),

    //28x Injection result
    MSG_281(281, "Get injection result list successfully"),
    MSG_282(282, "Get injection result successfully"),
    MSG_283(283, "Add new injection result successfully"),
    MSG_284(284, "Update injection result successfully"),

    //29x other


    //3xx error
    MSG_301(301, "File uploaded fail"),
    MSG_401(401, "No data found"),
    MSG_218(218, "Database connection error"),
    MSG_219(219, "No data found"),

    MSG_400(401, "Unexpected exception"),
    MSG_402(402, "Delete failed"),
    MSG_404(404, "Resource not found"),




    ;
    //more messages here

    private final int code;
    private final String message;

    Message(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

