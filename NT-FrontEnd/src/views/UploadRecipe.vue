<template>
    <el-card class="upload-recipe-card" shadow="hover">
        <div class="upload-header" style="display:flex; align-items:center; margin-bottom:20px;">
            <el-button type="text" @click="goBack" style="margin-right:10px; font-size:16px;">
                ← 返回
            </el-button>
            <br>
            <h2 style="flex:1; text-align:center; margin:0;">上传食谱</h2>
        </div>
        <el-form :model="form" ref="formRef" label-width="90px" :label-position="'top'">

            <!-- 菜谱名称 -->
            <el-form-item label="食谱名称" :rules="[{ required: true, message: '请输入食谱名称', trigger: 'blur' }]">
                <el-input v-model="form.name" placeholder="请输入食谱名称"></el-input>
            </el-form-item>

            <!-- 菜谱描述 -->
            <el-form-item label="描述">
                <el-input type="textarea" v-model="form.description" placeholder="请输入食谱描述" rows="3"></el-input>
            </el-form-item>

            <!-- 烹饪时间 -->
            <el-form-item label="烹饪时间(分钟)">
                <el-input-number v-model="form.cookTime" :min="1" style="width: 100%;"></el-input-number>
            </el-form-item>

            <!-- 膳食类型 -->
            <el-form-item label="膳食类型">
                <el-select v-model="form.mealType" placeholder="选择膳食类型" style="width: 100%;">
                    <el-option label="早餐" value="breakfast"></el-option>
                    <el-option label="午餐" value="lunch"></el-option>
                    <el-option label="晚餐" value="dinner"></el-option>
                    <el-option label="甜点" value="dessert"></el-option>
                </el-select>
            </el-form-item>

            <!-- 封面图片 -->
            <el-form-item label="封面图片">
                <el-upload class="upload-demo" list-type="picture-card" :show-file-list="true"
                    :before-upload="beforeUpload" :http-request="uploadImage">
                    <i class="el-icon-plus"></i>
                </el-upload>
                <!-- <div v-if="form.coverUrl" style="margin-top:5px;">已上传图片: {{ form.coverUrl }}</div> -->
            </el-form-item>

            <!-- 食材列表 -->
            <el-form-item label="食材">
                <div v-for="(ingredient, index) in form.ingredients" :key="index" class="ingredient-row">
                    <span style="flex: 2;">{{ ingredient.foodName || '未选择' }}</span>
                    <el-input-number v-model="ingredient.weight" :min="1" placeholder="克"
                        style="flex: 1.5; margin-left: 5px;"></el-input-number>
                    <el-button type="danger" @click="removeIngredient(index)"
                        style="margin-left: 10px; flex-shrink: 0;">
                        <i class="el-icon-delete"></i> 移除
                    </el-button>
                </div>
                <el-button type="primary" plain icon="el-icon-plus" @click="openFoodDialog"
                    style="margin-top: 10px; width: 100%;">
                    添加食材
                </el-button>
            </el-form-item>

            <!-- 食材选择弹窗 -->
            <el-dialog title="选择食材" v-model="foodDialogVisible" width="600px">
                <el-input v-model="searchName" placeholder="输入食材名称搜索" clearable style="margin-bottom: 10px;"
                    @keyup.enter="loadFoods(1)">
                    <template #append>
                        <el-button @click="loadFoods(1)">搜索</el-button>
                    </template>
                </el-input>

                <el-table :data="foods" style="width: 100%" @row-dblclick="handleSelectFood">
                    <el-table-column prop="name" label="名称" />
                    <el-table-column prop="caloriesPer100g" label="热量(kcal/100g)" width="160" />
                    <el-table-column label="操作" width="120">
                        <template #default="scope">
                            <el-button type="primary" size="small" @click="handleSelectFood(scope.row)">
                                选择
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total"
                    layout="prev, pager, next, jumper" @current-change="loadFoods"
                    style="margin-top: 10px; text-align: right;" />
            </el-dialog>

            <!-- 烹饪步骤 -->
            <el-form-item label="步骤">
                <div v-for="(step, index) in form.steps" :key="index" class="step-row">
                    <el-input v-model="form.steps[index]" placeholder="输入烹饪步骤" style="flex: 1;"></el-input>
                    <el-button type="danger" @click="removeIngredient(index)"
                        style="margin-left: 10px; flex-shrink: 0;">
                        <i class="el-icon-delete"></i> 移除
                    </el-button>
                </div>
                <el-button type="primary" plain icon="el-icon-plus" @click="addStep"
                    style="margin-top: 10px; width: 100%;">添加步骤</el-button>
            </el-form-item>

            <!-- 提交按钮 -->
            <el-form-item>
                <el-button type="primary" @click="submitForm" style="width: 100%;">提交食谱</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { uploadImageService, uploadRecipeService } from '../api/recipe';
import { fetchFoodsService } from '../api/food';
import { useRouter } from "vue-router";
const router = useRouter();

const formRef = ref(null);

const form = reactive({
    name: '',
    description: '',
    cookTime: null,
    mealType: '',
    coverUrl: '',           // 封面图片 URL
    ingredients: [],
    steps: [],
});

// 弹窗状态
const foodDialogVisible = ref(false)
const foods = ref([])
const page = ref(1)
const size = ref(10)
const searchName = ref('')

// 打开弹窗
const openFoodDialog = () => {
    foodDialogVisible.value = true
    loadFoods(1)
}

// 加载食物数据
const loadFoods = async (newPage = page.value) => {
    page.value = newPage
    const res = await fetchFoodsService(page.value, size.value, searchName.value)
    console.log('获取食物列表成功：', res);
    foods.value = res.data || [] // 注意根据后端返回结构调整
}

// 选择食材
const handleSelectFood = (food) => {
    form.ingredients.push({
        foodId: food.id,
        foodName: food.name,
        weight: null
    })
    foodDialogVisible.value = false
}

// 删除食材
const removeIngredient = (index) => {
    form.ingredients.splice(index, 1)
}

// 添加/删除步骤
const addStep = () => {
    form.steps.push('');
};
const removeStep = (index) => {
    form.steps.splice(index, 1);
};

const uploadImage = async (option) => {
    const file = option.file;

    try {
        const res = await uploadImageService(file); // 调用后端接口
        console.log('上传成功', res);
        form.coverUrl = res.data;

        // 上传成功
        option.onSuccess(res);
        ElMessage.success('图片上传成功');
    } catch (err) {
        console.error('上传失败', err);
        option.onError(err);
        ElMessage.error('图片上传失败');
    }
};

// 上传前校验（可选）
const beforeUpload = (file) => {
    const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png';
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isJPGorPNG) {
        ElMessage.error('上传封面图片只能是 JPG/PNG 格式!');
    }
    if (!isLt2M) {
        ElMessage.error('上传封面图片大小不能超过 2MB!');
    }
    return isJPGorPNG && isLt2M;
};

// 提交表单
const submitForm = async () => {
    if (!form.name) {
        ElMessage.warning('请输入菜谱名称');
        return;
    }

    try {
        const payload = {
            name: form.name,
            description: form.description,
            cookTime: form.cookTime,
            mealType: form.mealType,
            imageUrl: form.coverUrl,      // 封面图片 URL
            ingredients: form.ingredients,
            steps: form.steps,
        };

        // 使用 uploadRecipeService 上传
        await uploadRecipeService(payload);

        ElMessage.success('食谱上传成功');

        // 重置表单
        form.name = '';
        form.description = '';
        form.cookTime = null;
        form.mealType = '';
        form.coverUrl = '';
        form.ingredients = [];
        form.steps = [];
    } catch (err) {
        console.error(err);
        ElMessage.error('上传失败，请重试');
    }
};

const goBack = () => {
    router.back(); // 返回上一页
}
</script>

<style scoped>
.upload-recipe-card {
    width: 95%;
    max-width: 500px;
    margin: 20px auto;
    padding: 20px;
}

.ingredient-row,
.step-row {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    flex-wrap: wrap;
    width: 100%;
    /* 占满父容器宽度 */

}
</style>
