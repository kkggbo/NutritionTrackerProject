<template>
  <div class="add-food-container">
    <h2 class="page-title">🍽 添加新食物</h2>

<el-form
  :model="form"
  :rules="rules"
  ref="foodFormRef"
  label-width="100px"
  label-position="left"
  class="food-form"
>
  <el-form-item label="食物名称" prop="name">
    <div class="form-item-column">
      <el-input
        v-model="form.name"
        placeholder="请输入食物名称"
        class="name-input tall-input"
      />
    </div>
  </el-form-item>

  <div class="input-tip">请输入每百克营养成分</div>

  <el-form-item label="热量 (kcal)" prop="caloriesPer100g">
    <el-input-number
      v-model="form.caloriesPer100g"
      :min="0"
      class="number-input tall-input"
    />
  </el-form-item>

  <el-form-item label="碳水 (g)" prop="carbsPer100g">
    <el-input-number
      v-model="form.carbsPer100g"
      :min="0"
      class="number-input tall-input"
    />
  </el-form-item>

  <el-form-item label="蛋白质 (g)" prop="proteinPer100g">
    <el-input-number
      v-model="form.proteinPer100g"
      :min="0"
      class="number-input tall-input"
    />
  </el-form-item>

  <el-form-item label="脂肪 (g)" prop="fatPer100g">
    <el-input-number
      v-model="form.fatPer100g"
      :min="0"
      class="number-input tall-input"
    />
  </el-form-item>
</el-form>

    
      <el-form-item class="button-group">
        <el-button type="primary" @click="submitForm" class="btn">提交</el-button>
        <el-button @click="resetForm" class="btn">重置</el-button>
        <el-button type="info" @click="goBack" class="btn">返回</el-button>
      </el-form-item>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { addFoodService } from '@/api/food'

const router = useRouter()

const goBack = () => {
  router.back()  
}

const form = reactive({
  name: '',
  caloriesPer100g: null,
  carbsPer100g: null,
  proteinPer100g: null,
  fatPer100g: null
})

const rules = {
  name: [{ required: true, message: '请输入食物名称', trigger: 'blur' }],
  caloriesPer100g: [{ required: true, message: '请输入热量', trigger: 'blur' }],
  carbsPer100g: [{ required: true, message: '请输入碳水含量', trigger: 'blur' }],
  proteinPer100g: [{ required: true, message: '请输入蛋白质含量', trigger: 'blur' }],
  fatPer100g: [{ required: true, message: '请输入脂肪含量', trigger: 'blur' }]
}

const foodFormRef = ref()

const submitForm = async () => {
  try {
    const valid = await foodFormRef.value.validate();
    if (valid) {
      const res = await addFoodService(form);
      if (res.code === 1) {
        ElMessage.success("食物添加成功");
        resetForm();
      } else {
        ElMessage.warning(res.msg || "添加失败");
      }
    }
  } catch (err) {
    console.error(err);
    ElMessage.error("添加失败，请检查表单或稍后再试");
  }
};


const resetForm = () => {
  foodFormRef.value.resetFields()
}
</script>

<style scoped>
.add-food-container {
  display: flex;
  flex-direction: column;
  max-width: 500px;
  margin: 30px auto;
  padding: 20px 16px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 100px);
}

.page-title {
  text-align: center;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.food-form {
  width: 80%;
  padding: 20px;
}

.number-input {
  width: 100%;
  height: 50px;
}

.name-input {
  width: 100%;
  height: 50px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #eee;
  margin-top: auto;
  background-color: #fff; 
}

.btn {
  flex: 1;
  margin-right: 10px;
  height: 60px;
}
.btn:last-child {
  margin-right: 0;
}

.el-form-item {
  margin-bottom: 20px;
  align-items: center;
}

.form-item-column {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.input-tip {
  margin-top: 6px;
  margin-bottom: 6px;
  font-size: 24px;
  font-weight: bold;
  color: #888;
}

.tall-input {
  height: 50px;
  line-height: 50px;
  width: 100%;
}

.el-input-number .el-input__inner {
  height: 50px;
  line-height: 50px;
}

.el-input .el-input__inner {
  height: 50px;
  line-height: 50px;
}

.food-form {
  flex: 1;
  overflow-y: auto;
}


</style>
