//定制请求的实例

//导入axios  npm install axios
import axios from 'axios';
import { ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token.js'
import router from '@/router'
//定义一个变量,记录公共的前缀  ,  baseURL
const baseURL = '/api';
const instance = axios.create({baseURL})


//添加响应拦截器
instance.interceptors.response.use(
    result=>{
        // 判断业务状态码
        if(result.data.code===1){
            return result.data;
        } else {
            if(result.data.code===401){
                // 跳转到登录页面
                router.push('/login')
            }
            ElMessage.error(result.data.msg?result.data.msg:'操作失败');
            return Promise.reject(result.data);
        }
    },
    err=>{
        console.log("Hello");
        ElMessage.error('服务异常');
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

// 添加请求拦截器
instance.interceptors.request.use(
    // 请求前的回调
    (config)=>{
        // 添加token
        const tokenStore = useTokenStore()
        const token = tokenStore.token

        if (token) {
            config.headers.Authorization = token
        }
        
        return config;
    },
    // 请求错误回调
    (err)=>{
        return Promise.reject(err);
    }
)

export default instance;