/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2025.  Lorem XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package io.github.xiaomisum.ryze.protocol.email;

/**
 * EMAIL 协议常量接口
 * <p>
 * 该接口定义了EMAIL协议相关的核心常量，包括EMAIL方法、状态码、请求头字段名、
 * 协议版本等。所有EMAIL协议相关的类都应该实现此接口以确保常量的一致性。
 * </p>
 *
 * @author xiaomi
 */
public interface EMailConstantsInterface {


    String DEF_REF_NAME_KEY = "__email_configure_element_default_ref_name__";

    /**
     * SMTP 服务器主机键名
     */
    String HOST = "host";

    /**
     * SMTP 服务器端口键名
     */
    String PORT = "port";

    /**
     * 是否启用 ssl
     */
    String USE_SSL = "use_ssl";

    /**
     * SMTP 服务器默认端口
     */
    int DEFAULT_PORT = 25;

    /**
     * 发件邮箱地址
     */
    String USERNAME = "username";

    /**
     * 发件邮箱密码
     */
    String PASSWORD = "password";

    /**
     * 收件人
     */
    String TO = "to";

    /**
     * 邮件标题
     */
    String TITLE = "title";

    /**
     * 邮件内容
     */
    String CONTENT = "content";

}