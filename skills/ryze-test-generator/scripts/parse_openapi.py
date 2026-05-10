#!/usr/bin/env python3
"""
OpenAPI/Swagger文档解析器
将OpenAPI规范转换为Ryze测试脚本所需的接口信息
"""

import json
import yaml
import sys
from typing import Dict, List, Any, Optional
from pathlib import Path


class OpenAPIParser:
    """解析OpenAPI 2.0/3.0文档"""
    
    def __init__(self, spec: Dict[str, Any]):
        self.spec = spec
        self.version = spec.get('openapi', spec.get('swagger', '2.0'))
        self.is_v3 = self.version.startswith('3')
        
    def parse(self) -> Dict[str, Any]:
        """解析完整的OpenAPI文档"""
        return {
            'info': self._parse_info(),
            'servers': self._parse_servers(),
            'paths': self._parse_paths(),
            'components': self._parse_components(),
            'security_schemes': self._parse_security_schemes()
        }
    
    def _parse_info(self) -> Dict[str, str]:
        """解析基本信息"""
        info = self.spec.get('info', {})
        return {
            'title': info.get('title', 'API'),
            'version': info.get('version', '1.0.0'),
            'description': info.get('description', '')
        }
    
    def _parse_servers(self) -> List[str]:
        """解析服务器地址"""
        if self.is_v3:
            servers = self.spec.get('servers', [])
            return [s.get('url', '') for s in servers]
        else:
            # Swagger 2.0
            host = self.spec.get('host', '')
            schemes = self.spec.get('schemes', ['http'])
            base_path = self.spec.get('basePath', '')
            if host:
                return [f"{schemes[0]}://{host}{base_path}"]
            return []
    
    def _parse_paths(self) -> List[Dict[str, Any]]:
        """解析所有路径和接口"""
        paths = []
        for path, path_item in self.spec.get('paths', {}).items():
            for method in ['get', 'post', 'put', 'delete', 'patch']:
                if method in path_item:
                    operation = path_item[method]
                    paths.append({
                        'path': path,
                        'method': method.upper(),
                        'summary': operation.get('summary', ''),
                        'tags': operation.get('tags', []),
                        'operation_id': operation.get('operationId', ''),
                        'parameters': self._parse_parameters(operation, path_item, path, method),
                        'request_body': self._parse_request_body(operation),
                        'responses': self._parse_responses(operation),
                        'security': operation.get('security', [])
                    })
        return paths
    
    def _parse_parameters(self, operation: Dict, path_item: Dict, path: str, method: str) -> List[Dict]:
        """解析参数"""
        params = []
        
        # 操作级参数
        for param in operation.get('parameters', []):
            params.append(self._normalize_param(param))
        
        # 路径级参数
        for param in path_item.get('parameters', []):
            # 检查是否已被操作级参数覆盖
            if not any(p['name'] == param.get('name') and p['in'] == param.get('in') 
                      for p in params):
                params.append(self._normalize_param(param))
        
        # 从路径中提取路径参数 (如 /users/{id})
        import re
        path_params = re.findall(r'\{(\w+)\}', path)
        for param_name in path_params:
            if not any(p['name'] == param_name and p['in'] == 'path' for p in params):
                params.append({
                    'name': param_name,
                    'in': 'path',
                    'required': True,
                    'schema': {'type': 'string'}
                })
        
        return params
    
    def _normalize_param(self, param: Dict) -> Dict:
        """标准化参数格式"""
        if self.is_v3:
            return {
                'name': param.get('name', ''),
                'in': param.get('in', ''),
                'required': param.get('required', False),
                'schema': param.get('schema', {}),
                'description': param.get('description', '')
            }
        else:
            # Swagger 2.0
            return {
                'name': param.get('name', ''),
                'in': param.get('in', ''),
                'required': param.get('required', False),
                'type': param.get('type', 'string'),
                'description': param.get('description', '')
            }
    
    def _parse_request_body(self, operation: Dict) -> Optional[Dict]:
        """解析请求体"""
        if self.is_v3:
            request_body = operation.get('requestBody', {})
            if request_body:
                content = request_body.get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    return {
                        'required': request_body.get('required', False),
                        'schema': schema,
                        'example': content['application/json'].get('example')
                    }
        else:
            # Swagger 2.0
            for param in operation.get('parameters', []):
                if param.get('in') == 'body':
                    return {
                        'required': param.get('required', False),
                        'schema': param.get('schema', {}),
                        'name': param.get('name', 'body')
                    }
        return None
    
    def _parse_responses(self, operation: Dict) -> Dict[str, Any]:
        """解析响应"""
        responses = {}
        for status_code, response in operation.get('responses', {}).items():
            if self.is_v3:
                content = response.get('content', {})
                if 'application/json' in content:
                    schema = content['application/json'].get('schema', {})
                    example = content['application/json'].get('example')
                    responses[status_code] = {
                        'description': response.get('description', ''),
                        'schema': schema,
                        'example': example
                    }
            else:
                # Swagger 2.0
                schema = response.get('schema', {})
                examples = response.get('examples', {})
                responses[status_code] = {
                    'description': response.get('description', ''),
                    'schema': schema,
                    'example': examples.get('application/json')
                }
        return responses
    
    def _parse_components(self) -> Dict[str, Any]:
        """解析组件定义"""
        if self.is_v3:
            return self.spec.get('components', {})
        else:
            return {
                'schemas': self.spec.get('definitions', {})
            }
    
    def _parse_security_schemes(self) -> Dict[str, Any]:
        """解析安全方案"""
        if self.is_v3:
            return self.spec.get('components', {}).get('securitySchemes', {})
        else:
            return self.spec.get('securityDefinitions', {})


def load_spec(file_path: str) -> Dict[str, Any]:
    """加载OpenAPI规范文件"""
    path = Path(file_path)
    with open(path, 'r', encoding='utf-8') as f:
        if path.suffix in ['.yaml', '.yml']:
            return yaml.safe_load(f)
        else:
            return json.load(f)


def extract_api_info(spec_path: str, output_path: Optional[str] = None) -> Dict[str, Any]:
    """提取API信息并可选输出到文件"""
    spec = load_spec(spec_path)
    parser = OpenAPIParser(spec)
    result = parser.parse()
    
    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)
    
    return result


if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python parse_openapi.py <spec_file> [output_file]")
        sys.exit(1)
    
    spec_file = sys.argv[1]
    output_file = sys.argv[2] if len(sys.argv) > 2 else None
    
    result = extract_api_info(spec_file, output_file)
    print(json.dumps(result, ensure_ascii=False, indent=2))
