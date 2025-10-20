import { withMermaid } from "vitepress-plugin-mermaid";

export default withMermaid({
  title: 'RYZE',
  description: '强大的多协议测试框架，让测试变得简单而优雅',
  
  base: '/',

  // 忽略死链接检查
  ignoreDeadLinks: true,
  
  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      { text: '文档', link: '/guide/introduction' },
      { text: '测试人员', link: '/tester/test-suite/test-suite-project' },
      { text: '开发者', link: '/developer/style' },
      { text: 'FAQ', link: '/faq/same' },
      { text: '6.0.6', link: 'https://github.com/XiaoMiSum/ryze' }
    ],
    
    sidebar: {
      '/guide/': [
        {
          text: '入门',
          items: [
            { text: '介绍', link: '/guide/introduction' },
            { text: '快速开始', link: '/guide/quick-start' },
            { text: '安装', link: '/guide/installation' }
          ]
        },
        {
          text: '核心概念',
          items: [
            { text: '测试集合', link: '/guide/concepts/test-suite' },
            { text: '取样器', link: '/guide/concepts/sampler' },
            { text: '前置处理器', link: '/guide/concepts/preprocessor' },
            { text: '后置处理器', link: '/guide/concepts/postprocessor' },
            { text: '验证器（断言）', link: '/guide/concepts/validator' },
            { text: '提取器', link: '/guide/concepts/extractor' },
            { text: '执行上下文', link: '/guide/concepts/context' }
          ]
        },
        {
          text: '配置语法',
          items: [
            { text: '配置文件结构', link: '/guide/configuration/structure' },
            { text: '模板语法', link: '/guide/configuration/template' },
            { text: '动态求值', link: '/guide/configuration/evaluation' }
          ]
        },
        {
          text: '协议支持',
          items: [
            { text: 'HTTP', link: '/guide/protocols/http' },
            { text: 'Dubbo', link: '/guide/protocols/dubbo' },
            { text: 'JDBC', link: '/guide/protocols/jdbc' },
            { text: 'Redis', link: '/guide/protocols/redis' },
            { text: 'Kafka', link: '/guide/protocols/kafka' },
            { text: 'RabbitMQ', link: '/guide/protocols/rabbitmq' },
            { text: 'ActiveMQ', link: '/guide/protocols/activemq' },
            { text: 'MongoDB', link: '/guide/protocols/mongodb' }
          ]
        },
        {
          text: '高级功能',
          items: [
            { text: '动态变量', link: '/guide/advanced/variables-and-functions' },
            { text: '配置元件', link: '/guide/advanced/configure-element' },
            { text: '拦截器', link: '/guide/advanced/interceptor' }
          ]
        },
        {
          text: '扩展机制',
          items: [
            { text: '自定义验证器', link: '/guide/extending/validator' },
            { text: '自定义提取器', link: '/guide/extending/extractor' },
            { text: '自定义函数', link: '/guide/extending/function' },
            { text: '自定义协议', link: '/guide/extending/protocol' }
          ]
        }
      ],
      
      '/tester/': [
        {
          text: '测试工程师指南',
          items: [
            {
              text: '测试集合', items: [
                { text: '测试集合（项目）', link: '/tester/test-suite/test-suite-project' },
                { text: '测试集合（模块）', link: '/tester/test-suite/test-suite-module' },
                { text: '测试集合（用例）', link: '/tester/test-suite/test-case' }
              ]
            },
            {
              text: '验证器（断言）', items: [
                { text: 'HTTP 验证器', link: '/tester/validator/http_assertion' },
                { text: 'JSON 验证器', link: '/tester/validator/json_assertion' },
                { text: '响应结果验证器', link: '/tester/validator/result_assertion' }
              ]
            },
            {
              text: '提取器', items: [
                { text: 'HTTP响应头提取器', link: '/tester/extractor/http_header_extractor' },
                { text: 'JSON 提取器', link: '/tester/extractor/json_extractor' },
                { text: '正则表达式提取器', link: '/tester/extractor/regex_extractor' },
                { text: '响应结果提取器', link: '/tester/extractor/result_extractor' }
              ]
            },
            {
              text: '协议指南', items: [
                { text: 'HTTP 指南', link: '/tester/protocols/http' },
                { text: 'JDBC 指南', link: '/tester/protocols/jdbc' },
                { text: 'Redis 指南', link: '/tester/protocols/redis' },
                { text: 'Dubbo 指南', link: '/tester/protocols/dubbo' },
                { text: 'Kafka 指南', link: '/tester/protocols/kafka' },
                { text: 'RabbitMQ 指南', link: '/tester/protocols/rabbitmq' },
                { text: 'ActiveMQ 指南', link: '/tester/protocols/activemq' },
                { text: 'MongoDB 指南', link: '/tester/protocols/mongodb' }
              ]
            },
            {
              text: '变量与函数', items: [
                { text: '变量', link: '/tester/variable/variables' },
                { text: '函数', link: '/tester/variable/functions' }
              ]
            },
            {
              text: '其他事项', items: [
                 { text: 'Gitlab集成',link: '/tester/other/gitlab' },
                 { text: '常见问题',link: '/faq/same' }
              ]
            }
          ]
        }
      ],
      
      '/developer/': [
        {
          text: '开发者指南',
          items: [
            { text: '编码规范', link: '/developer/style' },
            { text: '架构设计', link: '/developer/architecture' },
            { text: '新协议', link: '/developer/NewProtocol' },
            { text: '拦截器', link: '/developer/interceptor' },
            { text: '函数', link: '/developer/function' },
            { text: '验证器', link: '/developer/validator' },
            { text: '提取器', link: '/developer/extractor' }
          ]
        },
        {
          text: 'API',
          items: [
            { text: '参考信息', link: '/developer/api/reference' },
            { text: '核心 API', link: '/developer/api/core' },
            { text: '协议 API', link: '/developer/api/protocol' },
            { text: '测试组件 API', link: '/developer/api/test-element' },
            { text: '构建器 API', link: '/developer/api/builder' },
            { text: '断言与提取器 API', link: '/developer/api/builder' }
          ]
        }
      ],
      '/faq/': [
        {
          text: 'FAQ',
          link: '/faq/same'
        }
      ]
    },
    
    socialLinks: [
      { icon: 'github', link: 'https://github.com/XiaoMiSum/ryze' }
    ],
    
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2018-present XiaoMiSum'
    },
    
    editLink: {
      pattern: 'https://github.com/XiaoMiSum/ryze/edit/master/docs/:path',
      text: '在 GitHub 上编辑此页面'
    },
    lastUpdated: true,
    lastUpdatedText: '最后更新时间',
    
    outline: {
      level: [2, 3],
      label: '页面导航'
    }
  }
})